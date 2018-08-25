package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.utility.InputField;

import java.util.*;
import java.util.stream.Collectors;

/**
* Created by avery on 11/30/14.
*/
public class FamilyAdapter implements Family {
    private Program program;
    private String id;
    private final LinkedHashSet<Person> members = new LinkedHashSet<>();
    private Address address;
    private LinkedHashMap<InputField, String> registration = new LinkedHashMap<>();

    public FamilyAdapter(String id) {
        this.id = id;
    }

    public FamilyAdapter(Person firstPerson, Program program) {
        id = UUID.randomUUID().toString();
        this.program = program;
        addPerson(firstPerson);
    }

    public void addPerson(Person person) {
        members.add(person.getUpdater().asPerson());
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

    @Override
    public Optional<Clubber> findNthChild(int childNumber) {
        return getClubbers().stream().skip(childNumber - 1).findFirst();
    }

    @Override
    public void setValue(InputField field, String value) {
        registration.put(field, value);
    }

    @Override
    public String getValue(InputField field) {
        return registration.get(field);
    }

    @Override
    public Map<InputField, String> getValues() {
        return Collections.unmodifiableMap(registration);
    }

    public void setValues(Map<InputField,String> values) {
        this.registration = new LinkedHashMap<>(values);
    }

    @Override
    public boolean shouldInvite() {
        return this != null &&
                getParents().stream() // for now, parents only, until we get clubber features
                .filter(p -> p.getEmail().isPresent())
                .filter(p -> !p.getLogin().isPresent())
                .findAny()
                .isPresent();
    }

    @Override
    public Optional<Program> getProgram() {
        return Optional.ofNullable(program);
    }

    public void setProgram(Program program) {
        this.program = program;
    }
}
