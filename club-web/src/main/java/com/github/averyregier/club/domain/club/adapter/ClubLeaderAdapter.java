package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;

import java.time.LocalDate;
import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
class ClubLeaderAdapter extends PersonWrapper implements ClubLeader {
    private ClubAdapter club;
    private final Person person;

    public ClubLeaderAdapter(ClubAdapter club, Person person) {
        this.club = club;
        this.person = person;
        person.getUpdater().setLeader(this);
    }

    @Override
    public Program getProgram() {
        return club.getProgram();
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
    public Optional<Club> getClub() {
        return Optional.of(club);
    }

    @Override
    protected Person getPerson() {
        return person;
    }

    @Override
    public String getId() {
        return person.getId();
    }

    @Override
    public Name getName() {
        return person.getName();
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.of(this);
    }

}
