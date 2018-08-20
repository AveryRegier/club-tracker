package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.InputFieldGroup;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by avery on 7/11/15.
 */
public class PersistedProgram extends ProgramAdapter {
    private ClubFactory factory;
    private final String id;
    private final ClubManager manager;

    public PersistedProgram(ClubFactory factory, String locale, String orgName, Curriculum curriculum, String id, ClubManager manager,
                            Supplier<Map<RegistrationSection, InputFieldGroup>> registrationForm) {
        super(locale, orgName, curriculum, registrationForm);
        this.factory = factory;
        this.id = id;
        this.manager = manager;
        if(factory != null) setPersonManager(factory.getPersonManager());
    }

    @Override
    protected void syncFamily(Family family) {
        Connector connector = factory.getConnector();
        family.getAddress().ifPresent(a->
            new AddressBroker(factory.getConnector()).persist(a)
        );
        new FamilyBroker(factory).persist(family);
        new FamilyRegistrationBroker(factory).persist(family);
        family.getParents().forEach(p -> {
            new PersonBroker(factory).persist(p);
            new ParentBroker(connector).persist(p);
            new PersonRegistrationBroker(factory).persist(p);
        });
        family.getClubbers().forEach(c -> {
            new PersonBroker(factory).persist(c);
            new ClubberBroker(factory).persist(c);
            new PersonRegistrationBroker(factory).persist(c);
        });
    }

    @Override
    protected ClubberAdapter createClubber() {
        return new PersistedClubber(factory, factory.getPersonManager().createPerson());
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

    @Override
    protected TreeSet<ClubAdapter> loadClubs() {
        return new ClubBroker(factory.getConnector())
                .findChildren(this, factory.getClubManager())
                .stream()
                .map(c->(ClubAdapter)c)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public Set<Listener> getListeners() {
        super.getListeners().addAll(new ListenerBroker(factory.getConnector())
                .find(this, factory.getPersonManager()));
        return super.getListeners();
    }

    @Override
    protected InputFieldGroup persist(InputFieldGroup group) {
        new InputFieldGroupBroker(factory.getConnector()).persist(group);
        new RegistrationFormBroker(factory.getConnector()).persist(this, group);
        return group;
    }

    @Override
    protected void persist(ClubYear clubYear) {
        new ClubYearBroker(factory.getConnector()).persist(clubYear);
        MeetingBroker meetingBroker = new MeetingBroker(factory.getConnector());
        clubYear.getSchedule().getList().forEach(meeting->{
            meetingBroker.persist(meeting.getEvent());
        });
    }
}
