package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.ClubGroup;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.Policy;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by avery on 12/16/14.
 */
public abstract class ClubGroupAdapter implements ClubGroup {
    private Set<Listener> listeners = new TreeSet<>();
    private AtomicReference<EnumSet<Policy>> policies = new AtomicReference<>();

    @Override
    public Set<Listener> getListeners() {
        return listeners;
    }

    @Override
    public Listener recruit(Person person) {
        getListeners();
        if (getParentGroup().isPresent()) {
            getParentGroup().get().recruit(person);
        }
        ListenerAdapter listener = findListener(person);
        listener.setClubGroup(this);
        persist(listener);
        listeners.add(listener);
        return listener;
    }

    @Override
    public <T> Stream<T> findPolicy(Function<Policy, Optional<T>> policy) {
        return Policy.findPolicies(loadPolicies(), policy);
    }

    protected EnumSet<Policy> loadPolicies() {
        policies.compareAndSet(null, EnumSet.noneOf(Policy.class));
        return policies.get();
    }

    @Override
    public void replacePolicies(Collection<Policy> policies) {
        persist(policies);
        this.policies.set(EnumSet.copyOf(policies));
    }

    protected void persist(Collection<Policy> policies) {
    }

    @Override
    public Collection<Policy> getPolicies() {
        return EnumSet.copyOf(policies.get());
    }

    protected void persist(Listener listener) {
    }

    private ListenerAdapter findListener(Person person) {
        return (ListenerAdapter) person.asListener().orElseGet(() -> new ListenerAdapter(person));
    }
}
