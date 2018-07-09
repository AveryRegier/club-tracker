package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.Setting;

public class SettingAdapter<T> implements Setting<T> {
    private final Type<T> type;
    private final String key;
    private final T value;

    public SettingAdapter(Type<T> type, String key, T value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    @Override
    public Type<T> getType() {
        return type;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public T getValue() {
        return value;
    }
}
