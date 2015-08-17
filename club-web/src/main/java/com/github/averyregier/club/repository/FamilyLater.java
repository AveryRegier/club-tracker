package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.FamilyBroker;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Parent;
import com.github.averyregier.club.domain.club.Person;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 8/13/15.
 */
public class FamilyLater implements Family {
    private final ClubFactory factory;
    private final String familyId;
    private Optional<Family> family;

    public FamilyLater(ClubFactory factory, String familyId) {
        this.factory = factory;
        this.familyId = familyId;
    }

    @Override
    public Set<Parent> getParents() {
        return getFamily().map(Family::getParents).orElse(Collections.emptySet());
    }

    @Override
    public void addPerson(Person person) {
        getFamily().ifPresent(f -> f.addPerson(person));
    }

    @Override
    public Set<Clubber> getClubbers() {
        return getFamily().map(Family::getClubbers).orElse(Collections.emptySet());
    }

    @Override
    public String getId() {
        return familyId;
    }

    public synchronized Optional<Family> getFamily() {
        if(family == null) {
            family = loadFamily();
        }
        return family;
    }

    private Optional<Family> loadFamily() {
        return PersistedPersonManager.loadFamily(
                familyId,
                new FamilyBroker(factory.getConnector()),
                factory.getPersonManager());
    }
}
