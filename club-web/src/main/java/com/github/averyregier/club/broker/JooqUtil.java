package com.github.averyregier.club.broker;

import org.jooq.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by avery on 2/19/15.
 */
public class JooqUtil {

    public static class MapBuilder<R extends Record> {
        private Map<TableField<R, ?>, Object> map = new LinkedHashMap<>();

        /**
         * Set a value for a field in the <code>INSERT</code> statement.
         */
        <T> MapBuilder<R> set(TableField<R, T> field, T value) {
            map.put(field, value);
            return this;
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement.
         */
        <T> MapBuilder<R> set(TableField<R, T> field, Optional<T> value) {
            map.put(field, value.orElse(null));
            return this;
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement.
         */
        <T> MapBuilder<R> set(TableField<R, T> field, Field<T> value) {
            map.put(field, value);
            return this;
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement.
         */
        <T> MapBuilder<R> set(TableField<R, T> field, Select<? extends Record1<T>> value) {
            map.put(field, value);
            return this;
        }

        public Map<TableField<R, ?>, Object> build() {
            return map;
        }

    }

    public static <R extends Record> MapBuilder<R> map() {
        return new MapBuilder<>();
    }
}
