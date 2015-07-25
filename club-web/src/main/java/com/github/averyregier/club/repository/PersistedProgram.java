package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;

import java.util.Optional;

/**
 * Created by avery on 7/11/15.
 */
public class PersistedProgram extends ProgramAdapter {
    private Connector connector;
    private final String id;

    public PersistedProgram(Connector connector, String locale, String orgName, Optional<Club> club, String id) {
        super(locale, orgName, club);
        this.connector = connector;
        this.id = id;
    }

    @Override
    protected void syncFamily(Family family) {
        new FamilyBroker(connector).persist(family);
        family.getParents().forEach(p -> {
            new PersonBroker(connector).persist(p);
            new ParentBroker(connector).persist(p);
        });
        family.getClubbers().forEach(c -> {
            new PersonBroker(connector).persist(c);
            new ClubberBroker(connector).persist(c);
        });
    }

    @Override
    public String getId() {
        return id;
    }
}
