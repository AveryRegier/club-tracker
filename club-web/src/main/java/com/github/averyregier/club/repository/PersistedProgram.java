package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.Curriculum;

/**
 * Created by avery on 7/11/15.
 */
public class PersistedProgram extends ProgramAdapter {
    private ClubFactory factory;
    private final String id;
    private final ClubManager manager;

    public PersistedProgram(ClubFactory factory, String locale, String orgName, Curriculum curriculum, String id, ClubManager manager) {
        super(locale, orgName, curriculum);
        this.factory = factory;
        this.id = id;
        this.manager = manager;
    }

    @Override
    protected void syncFamily(Family family) {
        Connector connector = factory.getConnector();
        new FamilyBroker(connector).persist(family);
        family.getParents().forEach(p -> {
            new PersonBroker(factory).persist(p);
            new ParentBroker(connector).persist(p);
        });
        family.getClubbers().forEach(c -> {
            new PersonBroker(factory).persist(c);
            new ClubberBroker(factory).persist(c);
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

    @Override
    protected void persist(Listener listener) {
        new ListenerBroker(factory.getConnector()).persist(listener);
    }

    @Override
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
        ClubLeader leader = super.assign(person, role);
        new LeaderBroker(factory.getConnector()).persist(leader);
        return leader;
    }
}
