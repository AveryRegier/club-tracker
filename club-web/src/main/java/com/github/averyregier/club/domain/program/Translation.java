package com.github.averyregier.club.domain.program;

/**
 * Created by rx39789 on 9/12/2014.
 */
public interface Translation {
    public static final Translation none = new Translation() {};

    public static Translation none() {
        return none;
    }
}
