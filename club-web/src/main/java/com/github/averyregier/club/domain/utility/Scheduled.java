package com.github.averyregier.club.domain.utility;

import java.time.LocalDate;

public class Scheduled<C extends HasId, E extends HasId> {
    private C container;
    private LocalDate date;
    private E item;

    public Scheduled(C container, LocalDate date, E item) {
        this.container = container;
        this.date = date;
        this.item = item;
    }

    C getContainer() {
        return container;
    }

    LocalDate getDate() {
        return date;
    }

    E getEvent() {
        return item;
    }
}
