package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.FamilyBroker;
import com.github.averyregier.club.broker.PersonBroker;
import com.github.averyregier.club.broker.PersonRegistrationBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.UtilityMethods.orElseMaybe;

/**
 * Created by avery on 7/7/15.
 */
public class PersistedPersonManager extends PersonManager {
    private Supplier<PersonBroker> personBrokerSupplier;
    private Supplier<FamilyBroker> familyBrokerSupplier;
    private Supplier<PersonRegistrationBroker> registrationBrokerSupplier;

    public PersistedPersonManager(Supplier<PersonBroker> personBrokerSupplier,
                                  Supplier<FamilyBroker> familyBrokerSupplier,
                                  Supplier<PersonRegistrationBroker> registrationBrokerSupplier) {
        this.personBrokerSupplier = personBrokerSupplier;
        this.familyBrokerSupplier = familyBrokerSupplier;
        this.registrationBrokerSupplier = registrationBrokerSupplier;
    }

    @Override
    public Optional<Person> lookup(String id) {
        return Optional.ofNullable(
                people.computeIfAbsent(id,
                        key -> getPersonBroker()
                                .find(id)
                                .map(this::addRegistrationFields)
                                .orElse(null)));
    }

    @Override
    public Collection<Person> getPeople() {
        return getPersonBroker()
                .findAll()
                .stream()
                .peek(this::addRegistrationFields)
                .collect(Collectors.toList());
    }

    private Person addRegistrationFields(Person p) {
        ((PersonAdapter) p).setValues(
                registrationBrokerSupplier.get().getRegistration(p.getId()));
        return p;
    }

    private PersonBroker getPersonBroker() {
        return personBrokerSupplier.get();
    }

    @Override
    protected void update(Person person) {
        getPersonBroker().persist(person);
    }

    @Override
    public Optional<Family> lookupFamily(String familyId) {
        return orElseMaybe(super.lookupFamily(familyId), ()-> getFamily(familyId));
    }

    private Optional<Family> getFamily(String familyId) {
        return loadFamily(familyId, familyBrokerSupplier.get(), this);
    }

    public static Optional<Family> loadFamily(String familyId, FamilyBroker familyBroker, PersonManager personManager) {
        List<Person> members = familyBroker.getAllFamilyMembers(familyId)
                .map(personManager::lookup)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return members.isEmpty() ?
                Optional.empty() :
                Optional.of(familyBroker.getPersistedFamily(familyId, members));
    }

}
