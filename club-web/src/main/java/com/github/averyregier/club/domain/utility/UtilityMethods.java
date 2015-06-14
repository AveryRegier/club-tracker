package com.github.averyregier.club.domain.utility;

import org.jooq.exception.DataAccessException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by avery on 10/9/14.
 */
public class UtilityMethods {
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T item, T... items) {
        if(items != null) {
            T[] result = (T[]) Array.newInstance(items.getClass().getComponentType(), items.length + 1);
            result[0] = item;
            System.arraycopy(items, 0, result, 1, items.length);
            return result;
        } else if(item != null) {
            T[] result = (T[]) Array.newInstance(item.getClass(), 1);
            result[0] = item;
            return result;
        } else return null;
    }

    public static <T> List<T> concat(T item, List<T> items) {
        if(items != null) {
            ArrayList<T> result = new ArrayList<>(items);
            result.add(0, item);
            return result;
        } else if(item != null) {
            return Arrays.asList(item);
        } else return Collections.emptyList();
    }

    public static <T> Collector<T, ?, LinkedHashSet<T>> toLinkedSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    public static <T> LinkedHashSet<T> asLinkedSet(T... elements) {
        LinkedHashSet<T> set = new LinkedHashSet<>();
        for(T element: elements) {
            set.add(element);
        }
        return set;
    }

    public static <T> Set<T> asSet(T... elements) {
        return asLinkedSet(elements);
    }

    public static <R> Map<String, R> putAll(Map<String, R> model, String prefix, Map<String, R> subModel) {
        for(Map.Entry<String, R> e: subModel.entrySet()) {
            model.put(prefix + "." + e.getKey(), e.getValue());
        }
        return model;
    }

    public static <R> Map<String, R> prefix(String prefix, Map<String, R> subModel) {
        return putAll(new HashMap<>(), prefix, subModel);
    }

    public static <R> Map<String, R> subMap(String prefix, Map<String, R> map) {
        return map.entrySet().stream()
                .filter(e->e.getValue() != null)
                .filter(e -> e.getKey().split("\\.")[0].equals(prefix))
                .collect(Collectors.toMap(
                        e -> e.getKey().substring(prefix.length() + 1),
                        Map.Entry::getValue));
    }

    public static Map<String, String> transformToSingleValueMap(Map<String, String[]> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }

    public static <K,V> MapBuilder<K,V> map(K firstKey, V firstValue) {
        MapBuilder<K, V> builder = new MapBuilder<>();
        return builder.put(firstKey, firstValue);
    }

    public static <T> Optional<T> getOther(Set<T> set, T... items) {
        HashSet<T> toReturn = new HashSet<>(set);
        toReturn.removeAll(Arrays.asList(items));
        return toReturn.stream().findFirst();
    }

    public static <R> Optional<R> firstSuccess(Supplier<Optional<R>>... fns) {
        for(Supplier<Optional<R>> fn: fns) {
            Optional<R> result = fn.get();
            if(result.isPresent()) return result;
        }

        return Optional.empty();
    }

    public static <T> List<T> reverse(List<T> original){
        ArrayList<T> toReturn = new ArrayList<T>(original);
        Collections.reverse(toReturn);
        return toReturn;
    }

    public static String decode(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }
    }

    public static String killWhitespace(String s) {
        if(s != null) {
            s = s.trim();
            if(s.length() > 0) {
                return s;
            }
        }
        return null;
    }

    public static <T,R> Optional<R> optMap(Optional<T> in, Function<T, Optional<R>> fn) {
        return in.map(fn::apply).orElse(Optional.empty());
    }

    public static String convert(byte[] bytes) {
        if(bytes == null) return null;
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private static final Pattern localeMatcher = Pattern.compile
            ("^([^_]*)(_([^_]*)(_#(.*))?)?$");

    public static Locale parseLocale(String value) {
        if(value == null) return null;
        Matcher matcher = localeMatcher.matcher(value.replace('-', '_'));
        return matcher.find()
                ? isEmpty(matcher.group(5))
                ? isEmpty(matcher.group(3))
                ? isEmpty(matcher.group(1))
                ? null
                : new Locale(matcher.group(1))
                : new Locale(matcher.group(1), matcher.group(3))
                : new Locale(matcher.group(1), matcher.group(3),
                matcher.group(5))
                : null;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static java.sql.Date toSqlDate(LocalDate date) {
        if(date == null) return null;
        return new java.sql.Date(date.toEpochDay() * 24 * 60 * 60 * 1000);
    }
}
