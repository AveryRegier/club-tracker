package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.*;

import java.util.EnumSet;
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
    protected Program findProgram(String id) {
        return new OrganizationBroker(factory.getConnector()).find(id, factory.getClubManager()).orElse(null);
    }

    @Override
    protected Set<Listener> getListeners(Club club, Supplier<Set<Listener>> fn) {
        Set<Listener> set = new ListenerBroker(factory.getConnector()).find(club, factory.getPersonManager());
        fn.get().addAll(set);
        return set;
    }

    @Override
    protected void persist(ClubLeader leader) {
        new LeaderBroker(factory.getConnector()).persist(leader);
    }

    @Override
    protected void persist(PolicyHolder policyHolder) {
        new PolicyBroker(factory.getConnector()).persist(policyHolder);
    }

    @Override
    protected EnumSet<Policy> loadPolicies(PolicyHolder policyHolder) {
        return new PolicyBroker(factory.getConnector()).loadPolicies(policyHolder);
    }

    @Override
    protected void loadClubs() {
        new OrganizationBroker(factory.getConnector()).load(this);
    }
}
