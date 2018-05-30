package com.github.averyregier.club.domain;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.ClubberBroker;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.repository.PersistedProgram;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by avery on 9/6/2014.
 */
public class ClubManager {
    private Map<String, Club> clubs = new LinkedHashMap<>();
    protected ClubFactory factory;

    public ClubManager() {
        this.factory = null; // for testing scenarios only
    }

    public ClubManager(ClubFactory factory) {
        this.factory = factory;
    }

    public Optional<Club> lookup(String id) {
        doLoad();
        return Optional.ofNullable(clubs.computeIfAbsent(id, this::find));
    }

    protected Club find(String id) {
        return null;
    }
    protected Program findProgram(String id) {
        return null;
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
        return Programs.find(curriculum).map(s -> {
            if(parentId == null) {
                Program program = findProgram(id);
                if(program != null) return program;
            }
            return new PersistedClub(s, id, clubs.computeIfAbsent(parentId, this::find));
        });
    }

    public Optional<Club> constructClub(String id, Club parent, String curriculum) {

        return parent.getCurriculum().findCurriculum(curriculum)
                .map(s -> clubs.computeIfAbsent(id, a -> new PersistedClub(s, id, parent)));
    }

    public Program createProgram(String acceptLanguage, String organizationName, Curriculum curriculum, String id) {
        PersistedProgram program = new PersistedProgram(factory, acceptLanguage, organizationName, curriculum, id, this, HashMap::new);
        persist(program);
        clubs.put(program.getId(), program);
        return program;
    }
    public Program loadProgram(String acceptLanguage, String organizationName, Curriculum curriculum, String id,
                               Supplier<Map<RegistrationSection, InputFieldGroup>> registrationForm)
    {
        PersistedProgram program = new PersistedProgram(factory, acceptLanguage, organizationName, curriculum, id, this, registrationForm);
        clubs.put(program.getId(), program);
        return program;
    }

    public boolean hasPrograms() {
        doLoad();
        return clubs.values().stream().anyMatch(c -> c.asProgram().isPresent());
    }

    public Optional<InputField> getRegistrationField(String inputFieldId) {
        doLoad();
        return streamPrograms()
                .flatMap(p -> UtilityMethods.stream(p.findField(inputFieldId)))
                .findFirst();
    }

    private Stream<Program> streamPrograms() {
        return clubs.values().stream()
                .filter(c->c instanceof Program)
                .map(c -> (Program) c);
    }

    private synchronized void doLoad() {
        if(clubs.isEmpty()) {
            loadClubs();
            streamPrograms().forEach(Program::getClubs);
        }
    }

    protected void loadClubs() {

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
            return ClubManager.this.getListeners(this, super::getListeners);
        }

        @Override
        public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
            ClubLeader leader = super.assign(person, role);
            ClubManager.this.persist(leader);
            return leader;
        }

        @Override
        protected HashSet<Clubber> initializeClubbers() {
            return new LinkedHashSet<>(new ClubberBroker(factory).find(this));
        }
    }

    protected void persist(ClubLeader leader) {}

    protected Set<Listener> getListeners(Club club, Supplier<Set<Listener>> fn) {
        return fn.get();
    }
}
