package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.ClubGroup;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by avery on 12/16/14.
 */
public abstract class ClubGroupAdapter implements ClubGroup {
    private Set<Listener> listeners = new TreeSet<>();

    @Override
    public Set<Listener> getListeners() {
        return listeners;
    }

    @Override
    public Listener recruit(Person person) {
        getListeners();
        if(getParentGroup().isPresent()) {
            getParentGroup().get().recruit(person);
        }
        ListenerAdapter listener = findListener(person);
        listener.setClubGroup(this);
        persist(listener);
        listeners.add(listener);
        return listener;
    }

    protected void persist(Listener listener) {}

    private ListenerAdapter findListener(Person person) {
        return (ListenerAdapter) person.asListener().orElseGet(() -> new ListenerAdapter(person));
    }
}
