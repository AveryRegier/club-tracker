package com.github.averyregier.club.domain;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.repository.PersistedProgram;

import java.util.*;
import java.util.function.Supplier;

/**
 * Created by avery on 9/6/2014.
 */
public class ClubManager {
    private Map<String, Club> clubs = new LinkedHashMap<String, Club>();
    protected ClubFactory factory;

    public ClubManager() {
        this.factory = null; // for testing scenarios only
    }

    public ClubManager(ClubFactory factory) {
        this.factory = factory;
    }

    public Optional<Club> lookup(String id) {
        return Optional.ofNullable(clubs.computeIfAbsent(id, this::find));
    }

    protected Club find(String id) {
        return null;
    }

    public Collection<Club> getClubs() {
        return clubs.values();
    }

    public Club createClub(ClubGroup parent, Curriculum series) {
        String id = UUID.randomUUID().toString();
        ClubAdapter clubAdapter = new PersistedClub(series, id, parent);
        persist(clubAdapter);
        clubs.put(id, clubAdapter);
        return clubAdapter;
    }

    protected void persist(Club club) {}

    protected void persist(Listener listener) {}

    public Optional<Club> constructClub(String id, String parentId, String curriculum) {
        return Programs.find(curriculum).map(s ->
                new PersistedClub(s, id, (ClubGroup) clubs.get(parentId)));
    }

    public Program createProgram(String acceptLanguage, String organizationName, Curriculum curriculum, String id) {
        PersistedProgram program = new PersistedProgram(factory, acceptLanguage, organizationName, curriculum, id, this);
        persist(program);
        clubs.put(program.getId(), program);
        return program;
    }
    public Program loadProgram(String acceptLanguage, String organizationName, Curriculum curriculum, String id) {
        PersistedProgram program = new PersistedProgram(factory, acceptLanguage, organizationName, curriculum, id, this);
        clubs.put(program.getId(), program);
        return program;
    }

    private class PersistedClub extends ClubAdapter {
        private final String id;
        private final ClubGroup parent;

        public PersistedClub(Curriculum series, String id, ClubGroup parent) {
            super(series);
            this.id = id;
            this.parent = parent;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Optional<ClubGroup> getParentGroup() {
            return Optional.ofNullable(parent);
        }

        @Override
        public Program getProgram() {
            return getParentGroup()
                    .map(ClubGroup::getProgram)
                    .orElse(null);
        }

        @Override
        protected void persist(Listener listener) {
            ClubManager.this.persist(listener);
        }

        @Override
        public Set<Listener> getListeners() {
            return ClubManager.this.getListeners(this, ()->super.getListeners());
        }

        @Override
        public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
            ClubLeader leader = super.assign(person, role);
            ClubManager.this.persist(leader);
            return leader;
        }


    }

    protected void persist(ClubLeader leader) {}

    protected Set<Listener> getListeners(Club club, Supplier<Set<Listener>> fn) {
        return fn.get();
    }
}
