package com.github.averyregier.club.domain.utility;

import java.time.LocalDate;

public class Scheduled<C extends HasTimezone, E extends HasId> {
    private C container;
    private LocalDate date;
    private E item;

    public Scheduled(C container, LocalDate date, E item) {
        this.container = container;
        this.date = date;
        this.item = item;
    }

    public C getContainer() {
        return container;
    }

    public LocalDate getDate() {
        return date;
    }

    public E getEvent() {
        return item;
    }
}
