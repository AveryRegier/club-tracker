package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
class ParentAdapter extends PersonWrapper implements Parent {
    private final Person person;

    public ParentAdapter(Person person) {
        this.person = person;
        person.getUpdater().setParent(this);
    }

    @Override
    protected Person getPerson() {
        return person;
    }

    @Override
    public Optional<Parent> asParent() {
        return Optional.of(this);
    }
}