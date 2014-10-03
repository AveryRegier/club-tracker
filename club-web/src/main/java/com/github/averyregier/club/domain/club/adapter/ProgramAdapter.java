package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;

import java.util.*;

/**
* Created by avery on 9/23/14.
*/
public class ProgramAdapter implements Program {
    private final String acceptLanguage;
    private String organizationName;
    private final String curriculum;
    private SortedSet<Club> clubs = new TreeSet<>();

    public ProgramAdapter(String acceptLanguage, String organizationName, String curriculum) {
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationName;
        this.curriculum = curriculum;
    }

    @Override
    public Set<Club> getClubs() {
        return Collections.unmodifiableSortedSet(clubs);
    }

    @Override
    public RegistrationInformation createRegistrationForm() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(acceptLanguage);
    }

    @Override
    public void addClub(final Curriculum series) {
        clubs.add(new ClubAdapter(this, series));
    }

    @Override
    public void setName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public Set<Policy> getPolicies() {
        return null;
    }

    @Override
    public Optional<Program> asProgram() {
        return null;
    }

    @Override
    public String getShortName() {
        return organizationName;
    }

    @Override
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
        return null;
    }

    @Override
    public Curriculum getCurriculum() {
        return Programs.valueOf(curriculum).get();
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
        return null;
    }

    @Override
    public Program getProgram() {
        return null;
    }

    @Override
    public Optional<Club> asClub() {
        return null;
    }

    @Override
    public Set<Clubber> getClubbers() {
        return null;
    }

    @Override
    public int compareTo(Club o) {
        return 0;
    }
}