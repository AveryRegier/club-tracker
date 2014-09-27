package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.Optional;
import java.util.Set;

/**
* Created by avery on 9/26/14.
*/
class ClubAdapter implements Club {
    private ProgramAdapter programAdapter;
    private final Curriculum series;

    public ClubAdapter(ProgramAdapter programAdapter, Curriculum series) {
        this.programAdapter = programAdapter;
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
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
        return null;
    }

    @Override
    public Curriculum getCurriculum() {
        return series;
    }

    @Override
    public Set<Listener> getListeners() {
        return null;
    }

    @Override
    public Listener recruit(Person person) {
        return null;
    }

    @Override
    public Optional<ClubGroup> getParentGroup() {
        return Optional.of(getProgram());
    }

    @Override
    public Program getProgram() {
        return programAdapter;
    }

    @Override
    public Optional<Club> asClub() {
        return Optional.of(this);
    }

    @Override
    public Set<Clubber> getClubbers() {
        return null;
    }

    @Override
    public int compareTo(Club o) {
        return getShortName().compareTo(o.getShortName());
    }
}
