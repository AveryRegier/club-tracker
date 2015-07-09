package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.PersonBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Person;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by avery on 7/7/15.
 */
public class PersistedPersonManager extends PersonManager {
    private Supplier<PersonBroker> brokerSupplier;

    public PersistedPersonManager(Supplier<PersonBroker> brokerSupplier) {
        this.brokerSupplier = brokerSupplier;
    }

    @Override
    public Optional<Person> lookup(String id) {
        return Optional.ofNullable(
                people.computeIfAbsent(id,
                        key -> getPersonBroker().find(id).orElse(null)));
    }

    private PersonBroker getPersonBroker() {
        return brokerSupplier.get();
    }

    @Override
    protected void update(Person person) {
        getPersonBroker().persist(person);
    }
}
