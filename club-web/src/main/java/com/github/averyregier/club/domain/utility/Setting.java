package com.github.averyregier.club.domain.utility;

import java.util.Optional;

public interface Setting<T> {
    interface Type<T> {
        String marshall(T thing);

        Optional<T> unmarshall(String value);
    }

    Type<T> getType();

    String getKey();

    T getValue();

    default String marshall() {
        return getType().marshall(getValue());
    }
}
