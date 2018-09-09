package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* Created by avery on 12/16/14.
*/
public class ListenerAdapter extends PersonWrapper implements Listener {
    private final Person person;
    private ClubGroup clubGroup;

    public ListenerAdapter(Person person) {
        this.person = person.getUpdater().asPerson();
        person.getUpdater().setListener(this);
    }

    @Override
    public Set<Clubber> getQuickList() {
        return getClub().map(this::matchingClubbers).orElse(Collections.emptySet());
    }

    private Set<Clubber> matchingClubbers(Group cl) {
        List<ListenerGroupPolicy> policies = clubGroup.findPolicies(Policy::getListenerGroupPolicy).collect(Collectors.toList());

        Stream<Clubber> stream = cl.getClubbers().stream();
        for(ListenerGroupPolicy policy: policies) {
            stream = policy.limit(stream, this);
        }
        return stream

                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public LocalDate getBirthDate() {
        return null;
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public AgeGroup getCurrentAgeGroup() {
        return null;
    }

    @Override
    public Optional<Club> getClub() {
        return clubGroup.asClub();
    }

    public void setClubGroup(ClubGroup clubGroup) {
        this.clubGroup = clubGroup;
    }

    @Override
    protected Person getPerson() {
        return person;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Listener && getId().equals(((Listener) obj).getId());
    }

    @Override
    public int hashCode() {
        String id = getId();
        return id != null ? id.hashCode() : super.hashCode();
    }
}
