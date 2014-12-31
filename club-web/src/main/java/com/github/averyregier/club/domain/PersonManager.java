package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;

import java.util.*;

/**
 * Created by avery on 9/6/2014.
 */
public class PersonManager {
    private Map<String, Person> people = new LinkedHashMap<String, Person>();
    public Optional<Person> lookup(String id) {
        return Optional.ofNullable(people.get(id));
    }

    public Collection<Person> getPeople() {
        return people.values();
    }

    public Person createPerson() {
        String id = UUID.randomUUID().toString();
        PersonAdapter personAdapter = new PersonAdapter() {
            @Override
            public String getId() {
                return id;
            }
        };
        people.put(id, personAdapter);
        return personAdapter;
    }
}
