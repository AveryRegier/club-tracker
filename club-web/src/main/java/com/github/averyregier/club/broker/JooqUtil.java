package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.HasUUID;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.jooq.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
         * Set a value for a field in the <code>INSERT</code> statement, converting LocalDate to java.sql.Date.
         */
        MapBuilder<R> set(TableField<R, java.sql.Date> field, LocalDate value) {
            return set(field, UtilityMethods.toSqlDate(value));
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement, converting ZoneDateTime to java.sql.Timestamp.
         */
        MapBuilder<R> set(TableField<R, java.sql.Timestamp> field, ZonedDateTime value) {
            return set(field, Timestamp.from(value.toInstant()));
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement, converting LocalDate to java.sql.Date.
         */
        MapBuilder<R> setUUID(TableField<R, byte[]> field, Optional<String> value) {
            return set(field, value.map(String::getBytes).orElse(null));
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement, converting LocalDate to java.sql.Date.
         */
        MapBuilder<R> setHasUUID(TableField<R, byte[]> field, Optional<? extends HasUUID> value) {
            return set(field, value.map(v->v.getId().getBytes()).orElse(null));
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement, converting LocalDate to java.sql.Date.
         */
        MapBuilder<R> setHasUUID(TableField<R, byte[]> field, HasUUID value) {
            return set(field, value.getId());
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement, converting LocalDate to java.sql.Date.
         */
        MapBuilder<R> set(TableField<R, byte[]> field, HasId value) {
            return set(field, value != null ? value.getId() : null);
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement, converting LocalDate to java.sql.Date.
         */
        MapBuilder<R> setDate(TableField<R, java.sql.Date> field, Optional<LocalDate> value) {
            return set(field, value.map(UtilityMethods::toSqlDate).orElse(null));
        }

        /**
         * Set a value for a field in the <code>INSERT</code> statement, converting LocalDate to java.sql.Date.
         */
        MapBuilder<R> set(TableField<R, byte[]> field, String value) {
            return set(field, value != null ? value.getBytes() : null);
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

        public MapBuilder<R> setNull(TableField<R, ?>... fields) {
            if(fields != null) {
                for(TableField<R, ?> field: fields) {
                    map.put(field, null);
                }
            }
            return this;
        }
    }

    public static <R extends Record> MapBuilder<R> map() {
        return new MapBuilder<>();
    }
}
