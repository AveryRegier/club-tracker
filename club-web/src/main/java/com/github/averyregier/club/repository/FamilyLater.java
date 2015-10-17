package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.FamilyBroker;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.utility.InputField;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.github.averyregier.club.domain.utility.UtilityMethods.optMap;
import static com.github.averyregier.club.domain.utility.UtilityMethods.orNull;

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
    public Optional<Address> getAddress() {
        return optMap(getFamily(), Family::getAddress);
    }

    @Override
    public void setAddress(Address address) {
        getFamily().ifPresent(f->f.setAddress(address));
    }

    @Override
    public Optional<Clubber> findNthChild(int childNumber) {
        return optMap(getFamily(), (f)->f.findNthChild(childNumber));
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
                new FamilyBroker(factory),
                factory.getPersonManager());
    }

    @Override
    public void setValue(InputField field, String value) {
        getFamily().ifPresent(f->f.setValue(field, value));
    }

    @Override
    public String getValue(InputField field) {
        return orNull(getFamily().orElse(null), (f)->f.getValue(field));
    }

    @Override
    public Map<InputField, String> getValues() {
        return getFamily().map(Registered::getValues).orElse(Collections.emptyMap());
    }

    @Override
    public boolean shouldInvite() {
        return getFamily().map(Family::shouldInvite).orElse(false);
    }
}
