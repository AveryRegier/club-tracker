package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public class PersonManager {
    private Set<Person> people = new LinkedHashSet<Person>();
    public Optional<Person> lookup(String id) {
        return Optional.empty();
    }

    public Collection<Person> getPeople() {
        return people;
    }

    public Person createPerson() {
        PersonAdapter personAdapter = new PersonAdapter();
        people.add(personAdapter);
        return personAdapter;
    }
}
