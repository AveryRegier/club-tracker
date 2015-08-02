package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.Curriculum;

/**
 * Created by avery on 7/11/15.
 */
public class PersistedProgram extends ProgramAdapter {
    private Connector connector;
    private final String id;
    private final ClubManager manager;

    public PersistedProgram(Connector connector, String locale, String orgName, Curriculum curriculum, String id, ClubManager manager) {
        super(locale, orgName, curriculum);
        this.connector = connector;
        this.id = id;
        this.manager = manager;
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

    @Override
    protected ClubAdapter createClub(Curriculum series) {
        return (ClubAdapter)manager.createClub(this, series);
    }
}
