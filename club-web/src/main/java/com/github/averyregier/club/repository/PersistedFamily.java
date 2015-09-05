package com.github.averyregier.club.repository;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.FamilyAdapter;

import java.util.Collection;

/**
 * Created by avery on 7/11/15.
 */
public class PersistedFamily extends FamilyAdapter {
    private String id;

    public PersistedFamily(String id, Collection<Person> members) {
        super(id);
        members.stream().forEach(this::addPerson);
    }


}
