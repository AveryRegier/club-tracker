package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
* Created by avery on 9/26/14.
*/
abstract class ClubAdapter extends ClubGroupAdapter implements Club {
    private final Curriculum series;
    private Set<Clubber> clubbers = new HashSet<>();

    public ClubAdapter(Curriculum series) {
        this.series = series;
    }

    @Override
    public Set<Policy> getPolicies() {
        return null;
    }

    @Override
    public Optional<Program> asProgram() {
        return Optional.empty();
    }

    @Override
    public String getShortName() {
        return series.getShortCode();
    }

    @Override
    public Curriculum getCurriculum() {
        return series;
    }

    @Override
    public Optional<ClubGroup> getParentGroup() {
        return Optional.of(getProgram());
    }

    @Override
    public Optional<Club> asClub() {
        return Optional.of(this);
    }

    @Override
    public Set<Clubber> getClubbers() {
        return Collections.unmodifiableSet(clubbers);
    }

    @Override
    public int compareTo(Club o) {
        return getShortName().compareTo(o.getShortName());
    }

    void addClubber(ClubberAdapter clubber) {
        this.clubbers.add(clubber);
        clubber.setClub(this);
    }

    @Override
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
        ClubLeaderAdapter leader = new ClubLeaderAdapter(this, person);
        if(person.getLogin().isPresent()) {
            person.getLogin().get().setLeader(leader);
        }
        return leader;
    }
}
