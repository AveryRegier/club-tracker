package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.ClubBroker;
import com.github.averyregier.club.broker.ListenerBroker;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Listener;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by avery on 8/2/15.
 */
public class PersistedClubManager extends ClubManager {

    public PersistedClubManager(ClubFactory factory) {
        super(factory);
    }

    @Override
    protected void persist(Club club) {
        new ClubBroker(factory.getConnector()).persist(club);
    }

    @Override
    protected void persist(Listener listener) {
        new ListenerBroker(factory.getConnector()).persist(listener);
    }

    @Override
    protected Club find(String id) {
        return new ClubBroker(factory.getConnector()).find(id, this).orElse(null);
    }

    @Override
    protected Set<Listener> getListeners(Club club, Supplier<Set<Listener>> fn) {
        Set<Listener> set = new ListenerBroker(factory.getConnector()).find(club, factory.getPersonManager());
        fn.get().addAll(set);
        return set;
    }
}
