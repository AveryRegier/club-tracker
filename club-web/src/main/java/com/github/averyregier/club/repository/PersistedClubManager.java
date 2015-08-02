package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.ClubBroker;
import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Club;

/**
 * Created by avery on 8/2/15.
 */
public class PersistedClubManager extends ClubManager {

    private Connector connector;

    public PersistedClubManager(Connector connector) {
        this.connector = connector;
    }

    @Override
    protected void persist(Club club) {
        new ClubBroker(connector).persist(club);

    }

    @Override
    protected Club find(String id) {
        return new ClubBroker(connector).find(id, this).orElse(null);
    }
}
