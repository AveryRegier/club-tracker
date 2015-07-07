package com.github.averyregier.club.domain.utility;

import java.util.function.Supplier;

/**
 * Created by avery on 7/6/15.
 */
public class TrackedString extends TrackedField<String> {
    public TrackedString(String defaultValue) {
        super(defaultValue);
    }

    public void ifEmpty(Supplier<String> fn) {
        test(UtilityMethods::isEmpty, fn);
    }

}
