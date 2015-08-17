package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Parent;
import com.github.averyregier.club.domain.club.Person;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
public class ParentAdapter extends PersonWrapper implements Parent {
    private final Person person;

    public ParentAdapter(Person person) {
        this.person = person.getUpdater().asPerson();
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
