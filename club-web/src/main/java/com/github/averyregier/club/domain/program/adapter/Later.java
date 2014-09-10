package com.github.averyregier.club.domain.program.adapter;

/**
 * Created by rx39789 on 9/10/2014.
 */
public class Later<T> {
    private T value;
    public T get() {
        if(this.value == null) throw new IllegalStateException("value not yet set");
        return value;
    }
    public void set(T value) {
        if(this.value != null) throw new IllegalStateException("value is already set");
        this.value = value;
    }
}
