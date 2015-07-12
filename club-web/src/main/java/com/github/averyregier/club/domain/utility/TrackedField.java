package com.github.averyregier.club.domain.utility;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.averyregier.club.domain.utility.UtilityMethods.safeEquals;

/**
 * Created by avery on 7/6/15.
 */
public class TrackedField<T> {
    private T value;
    private T defaultValue;
    private boolean set = false;

    public TrackedField(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setValue(T value) {
        if (!safeEquals(value, getValue())) {
            forceSet(value);
        }
    }

    public void forceSet(T value) {
        this.value = value;
        this.set = true;
    }

    public T getValue() {
        if (set) return value;
        else return defaultValue;
    }

    public boolean isChanged() {
        return set;
    }

    public void test(Predicate<T> test, Supplier<T> fn) {
        if(test.test(getValue())) {
            setValue(fn.get());
        }
    }

    public static <T> TrackedField<T> track(T defaultValue) {
        return new TrackedField<>(defaultValue);
    }

    public static TrackedString track(String defaultValue ) {
        return new TrackedString(defaultValue);
    }
}
