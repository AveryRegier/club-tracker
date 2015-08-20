package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* Created by avery on 11/30/14.
*/
public class FamilyAdapter implements Family {
    private String id;
    private final LinkedHashSet<Person> members = new LinkedHashSet<>();
    private Address address;

    public FamilyAdapter(Person firstPerson) {
        id = UUID.randomUUID().toString();
        addPerson(firstPerson);
    }

    public FamilyAdapter(String id) {
        this.id = id;
    }

    public void addPerson(Person person) {
        members.add(person.getUpdater().asPerson());
    }

    public FamilyAdapter(String uuid, Person firsPerson) {
        this.id = uuid;
        addPerson(firsPerson);
    }

    @Override
    public Set<Parent> getParents() {
        return members.stream()
                .map(Person::asParent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<Clubber> getClubbers() {
        return members.stream()
                .map(Person::asClubber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Optional<Address> getAddress() {
        return Optional.ofNullable(address);
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }
}
