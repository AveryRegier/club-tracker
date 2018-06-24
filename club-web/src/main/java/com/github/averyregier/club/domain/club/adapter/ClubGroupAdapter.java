package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.ClubGroup;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.Policy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by avery on 12/16/14.
 */
public abstract class ClubGroupAdapter implements ClubGroup {
    private Set<Listener> listeners = new TreeSet<>();
    private EnumSet<Policy> policies = EnumSet.noneOf(Policy.class);

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
        return Policy.findPolicies(policies, policy);
    }

    @Override
    public void addPolicy(Policy policy) {
        this.policies.add(policy);
    }

    @Override
    public Collection<Policy> getPolicies() {
        return EnumSet.copyOf(policies);
    }

    protected void persist(Listener listener) {
    }

    private ListenerAdapter findListener(Person person) {
        return (ListenerAdapter) person.asListener().orElseGet(() -> new ListenerAdapter(person));
    }
}
