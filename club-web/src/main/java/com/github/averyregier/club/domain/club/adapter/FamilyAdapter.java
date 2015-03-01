package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
* Created by avery on 11/30/14.
*/
public class FamilyAdapter implements Family {
    private String id = UUID.randomUUID().toString();
    private final LinkedHashSet<Parent> parents = new LinkedHashSet<>();
    private final LinkedHashSet<Clubber> clubbers = new LinkedHashSet<>();

    public FamilyAdapter(Person firstPerson) {
        if(firstPerson.asParent().isPresent()) {
            parents.add(firstPerson.asParent().get());
        } else {
            clubbers.add(firstPerson.asClubber().get());
        }
    }

    protected void addParent(Parent parent) {
        parents.add(parent);
    }

    protected void addClubber(Clubber clubber) {
        clubbers.add(clubber);
    }

    @Override
    public Set<Parent> getParents() {
        return parents;
    }

    @Override
    public Family update(RegistrationInformation information) {
        return this;
    }

    @Override
    public RegistrationInformation getRegistration() {
        return null;
    }

    @Override
    public Set<Clubber> getClubbers() {
        return clubbers;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getShortCode() {
        return parents.stream().findFirst().map(p->"The "+p.getName().getSurname()+" Family").orElse(getId());
    }
}
