package com.github.averyregier.club.domain.utility;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubMember;
import org.jooq.exception.DataAccessException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
            return Collections.singletonList(item);
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

    public static <R> Map<String, R> subMap(String prefix, Map<?, R> map) {
        return map.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getKey().toString().split("\\.")[0].equals(prefix))
                .collect(Collectors.toMap(
                        e -> e.getKey().toString().substring(prefix.length() + 1),
                        Map.Entry::getValue));
    }

    public static <R> Map<R, String> toStrings(Map<R, ?> input) {
        return input.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
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

    public static void ifPresent(String string, Consumer<String> fn) {
        String s = killWhitespace(string);
        if(s != null && fn != null) {
            fn.accept(s);
        }
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
        return new java.sql.Date(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static boolean safeEquals(Object a, Object b) {
        if(a == b) return true;
        if(a == null) return false;
        if(b == null) return false;
        return a.equals(b);
    }

    public static <T> boolean change(T current, T newValue, Consumer<T> fn) {
        if(!safeEquals(current, newValue)) {
            fn.accept(newValue);
            return true;
        }
        return false;
    }

    public static String orEmpty(String surname) {
        return surname != null ? surname : "";
    }

    public static <T> String orEmpty(T object, Function<T,String> fn) {
        return object != null ? fn.apply(object) : "";
    }

    public static <T,R> R orNull(T object, Function<T,R> fn) {
        return object != null ? fn.apply(object) : null;
    }

    public static <T> Optional<T> orElseMaybe(Optional<T> opt, Supplier<Optional<T>> supplier) {
        if(!opt.isPresent()) {
            return supplier.get();
        }
        return opt;
    }

    public static <T> Supplier<T> once(Supplier<T> supplier) {
        return new SupplyOnce<>(supplier);
    }

    public static <T> Supplier<Optional<T>> setOnce(Supplier<Optional<T>> supplier, Consumer<T> setter) {
        return once(() -> {
            Optional<T> result = supplier.get();
            result.ifPresent(setter);
            return result;
        });
    }

    public static <T> Stream<T> stream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }

    public static <A,B> Optional<B> chain(Optional<A> start, Function<A,Optional<B>> fn) {
        if(start.isPresent()){
            return fn.apply(start.get());
        }
        return Optional.empty();
    }

    public static <A,B,C> Optional<C> chain(Optional<A> start, Function<A,Optional<B>> fn, Function<B, Optional<C>> fn2) {
        if(start.isPresent()){
            return chain(fn.apply(start.get()), fn2);
        }
        return Optional.empty();
    }

    public static <T> Stream<T> stream(Optional<? extends T> start, Function<T, Optional<? extends T>> nextFn) {
        return asStream(new Iterator<T>() {
            Optional<? extends T> current = start;

            @Override
            public boolean hasNext() {
                return current.isPresent();
            }

            @Override
            public T next() {
                T next = current.orElseThrow(NoSuchElementException::new);
                current = nextFn.apply(next);
                return next;
            }
        });
    }

    public static <A,B,R> Optional<R> both(Optional<A> a, Optional<B> b, BiFunction<A,B,R> fn) {
        if(a.isPresent() && b.isPresent()) {
            return Optional.ofNullable(fn.apply(a.get(), b.get()));
        }
        return Optional.empty();
    }

    public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

    public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static <A,B> void ifPresent(Optional<A> start, Function<A,Optional<B>> fn, Consumer<B> cn) {
        chain(start, fn).ifPresent(cn);
    }

    public static <A,B> B applyOrNull(A start, Function<A,B> fn) {
        if(start != null && fn != null) {
            return fn.apply(start);
        }
        return null;
    }

    public static <A,B> B applyOrDefault(A start, Function<A,B> fn, Function<A,B> defaultFn) {
        if (start != null) {
            if (fn != null) {
                return fn.apply(start);
            }
            if (defaultFn != null) {
                return defaultFn.apply(start);
            }
        }
        return null;
    }

    public static <T> boolean equalsAny(T value, T... expected) {
        for(T e: expected) {
            if(e.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <T> Optional<T> findLast(Stream<T> stream) {
        return stream.sorted(Collections.reverseOrder()).findFirst();
    }

    public static <T> Optional<T> findLast(Collection<T> collection) {
        return findLast(collection.stream());
    }

    public static <T> Optional<T> findPrevious(T item, Collection<? extends T> collection) {
        T previous = null;
        for(T o: collection) {
            if(o == item) return Optional.ofNullable(previous);
            previous = o;
        }
        return Optional.empty();
    }

    public static <T> Optional<T> findNext(T item, Collection<? extends T> collection) {
        boolean found = false;
        for(T o: collection) {
            if(found) return Optional.ofNullable(o);
            if(o == item) found = true;
        }
        return Optional.empty();
    }

    public static boolean notNull(Object o) {
        return o != null;
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static LocalDate findToday(ClubMember member) {
        return getToday(findZone(member));
    }

    public static LocalDate findToday(Optional<? extends HasTimezone> holder) {
        return getToday(findZone(holder));
    }

    public static LocalDate getToday(ZoneId zone) {
        return ZonedDateTime.now(zone).toLocalDate();
    }

    private static ZoneId findZone(ClubMember member) {
        Optional<Club> club = member.getClub();
        return findZone(club);
    }

    private static ZoneId findZone(Optional<? extends HasTimezone> holder) {
        return holder.map(h -> h.getTimeZone())
                .orElseGet(()-> getDefaultZone());
    }

    public static ZoneId getDefaultZone() {
        return ZoneId.of("CST6CDT");
    }

    public static Optional<LocalDate> parseDate(String date) {
        if(killWhitespace(date) == null) return Optional.empty();
        try {
            Date input = InputField.Type.dateFormat.get()
                    .parse(date);
            return Optional.of(input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    @SafeVarargs
    @SuppressWarnings({ "unchecked", "varargs" })
    public static <T> Stream<T> concat(Stream<? extends T>... streams) {
        return StreamConcatenation.concat(streams);
    }
}
