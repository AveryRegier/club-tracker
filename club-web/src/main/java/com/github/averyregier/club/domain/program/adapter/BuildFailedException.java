package com.github.averyregier.club.domain.program.adapter;

import java.util.function.Supplier;

public class BuildFailedException extends RuntimeException {
    public BuildFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuildFailedException(String message) {
        super(message);
    }

    public static <T> T attempt(String id, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            throw new BuildFailedException(id, t);
        }
    }
}
