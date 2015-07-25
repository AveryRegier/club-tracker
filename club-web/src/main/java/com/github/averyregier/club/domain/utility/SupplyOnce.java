package com.github.averyregier.club.domain.utility;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created by avery on 7/11/15.
 */
class SupplyOnce<T> implements Supplier<T> {
    private transient AtomicReference<T> supplied = new AtomicReference<>();
    private Supplier<T> supplier;

    public SupplyOnce(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return supplied.updateAndGet((x) -> x != null ? x : supplier.get());
    }
}
