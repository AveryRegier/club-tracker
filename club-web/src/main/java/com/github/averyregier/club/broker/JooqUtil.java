package com.github.averyregier.club.broker;

import org.jooq.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by avery on 2/19/15.
 */
public class JooqUtil {

    public static class MapBuilder<R extends Record> {
        private Map<TableField<R, String>, Object> map = new LinkedHashMap<>();

        /**
         * Set a value for a field in the <code>INSERT</code> statement.
         */
        <T> MapBuilder<R> set(TableField<R, String> field, T value) {
            map.put(field, value);
            return this;
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement.
         */
        <T> MapBuilder<R> set(TableField<R, String> field, Field<T> value) {
            map.put(field, value);
            return this;
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement.
         */
        <T> MapBuilder<R> set(TableField<R, String> field, Select<? extends Record1<T>> value) {
            map.put(field, value);
            return this;
        }

        public Map<TableField<R, String>, Object> build() {
            return map;
        }

    }

    public static <R extends Record> MapBuilder<R> map() {
        return new MapBuilder<>();
    }
}
