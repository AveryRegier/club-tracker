package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.averyregier.club.domain.utility.UtilityMethods.stream;

/**
 * Created by avery on 9/6/2014.
 */
public class PersonManager {
    protected Map<String, Person> people = new ConcurrentHashMap<>();
    public Optional<Person> lookup(String id) {
        return Optional.ofNullable(people.get(id));
    }

    public Collection<Person> getPeople() {
        return new TreeSet<>(people.values());
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

    public Optional<Family> lookupFamily(String familyId) {
        return people.values().stream()
                .filter(p->p.getUpdater().knowsFamily())
                .flatMap(p -> stream(p.getFamily()))
                .filter(f->f.getId().equals(familyId))
                .findFirst();
    }
}
