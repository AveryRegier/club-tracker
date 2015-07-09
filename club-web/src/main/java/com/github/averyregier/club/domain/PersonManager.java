package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by avery on 9/6/2014.
 */
public class PersonManager {
    protected Map<String, Person> people = new ConcurrentHashMap<>();
    public Optional<Person> lookup(String id) {
        return Optional.ofNullable(people.get(id));
    }

    public Collection<Person> getPeople() {
        return people.values();
    }

    public Person createPerson() {
        String id = UUID.randomUUID().toString();
        PersonAdapter person = new PersonAdapter(id);
        cacheNew(person);
        return person;
    }

    protected void cacheNew(PersonAdapter person) {
        update(person);
        people.put(person.getId(), person);
    }

    public void sync(Person person) {
        update(person);
    }

    protected void update(Person person) {}
}
