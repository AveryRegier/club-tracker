package com.github.averyregier.club.domain.utility;

public interface Setting<T> {
    interface Type<T> {
        String marshall(T thing);
        T unmarshall(String value);
    }

    Type<T> getType();
    String getKey();
    T getValue();

    default String marshall() {
        return getType().marshall(getValue());
    }
}
