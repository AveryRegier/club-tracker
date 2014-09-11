package com.github.averyregier.club.domain.program.adapter;

/**
 * Created by rx39789 on 9/10/2014.
 */
public class Later<T> {
    private T value;
    public T get() {
        assert(this.value != null);
        return value;
    }
    public void set(T value) {
        assert(this.value == null);
        this.value = value;
    }
}
