package com.github.averyregier.club.domain.utility;

import java.lang.reflect.Array;
import java.util.*;
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
}
