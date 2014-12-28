package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;

import java.time.LocalDate;
import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
class ClubLeaderAdapter implements ClubLeader {
    private ClubAdapter club;
    private final Person person;

    public ClubLeaderAdapter(ClubAdapter club, Person person) {
        this.club = club;
        this.person = person;
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
    public AgeGroup getCurrentAgeGroup() {
        return null;
    }

    @Override
    public Optional<Club> getClub() {
        return Optional.of(club);
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
    public Optional<Gender> getGender() {
        return person.getGender();
    }

    @Override
    public Optional<User> getLogin() {
        return person.getLogin();
    }

    @Override
    public Optional<String> getEmail() {
        return person.getEmail();
    }

    @Override
    public Optional<Parent> asParent() {
        return person.asParent();
    }

    @Override
    public Optional<Listener> asListener() {
        return person.asListener();
    }

    @Override
    public Optional<Clubber> asClubber() {
        return person.asClubber();
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.of(this);
    }

    @Override
    public Optional<Family> getFamily() {
        return person.getFamily();
    }
}
