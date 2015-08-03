package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.ClubBroker;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Club;

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
    protected Club find(String id) {
        return new ClubBroker(factory.getConnector()).find(id, this).orElse(null);
    }
}
