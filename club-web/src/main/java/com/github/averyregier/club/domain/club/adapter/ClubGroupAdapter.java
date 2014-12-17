package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.ClubGroup;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by avery on 12/16/14.
 */
public abstract class ClubGroupAdapter implements ClubGroup {
    private Set<Listener> listeners = new LinkedHashSet<>();

    @Override
    public Set<Listener> getListeners() {
        return listeners;
    }

    @Override
    public Listener recruit(Person person) {
        if(getParentGroup().isPresent()) {
            getParentGroup().get().recruit(person);
        }
        ListenerAdapter listener = findListener(person);
        listeners.add(listener);
        listener.setClubGroup(this);
        return listener;
    }

    private ListenerAdapter findListener(Person person) {
        if (person.asListener().isPresent()) {
            return (ListenerAdapter)person.asListener().get();
        } else {
            ListenerAdapter listener = new ListenerAdapter(person);
            if (person instanceof User) {
                ((User) person).setListener(listener);
            }
            return listener;
        }
    }
}
