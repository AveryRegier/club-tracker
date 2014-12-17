package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
* Created by avery on 12/16/14.
*/
class ListenerAdapter implements Listener {
    private final Person person;
    private ClubGroup clubGroup;

    public ListenerAdapter(Person person) {
        this.person = person;
    }

    @Override
    public Set<Clubber> getQuickList() {
        return null;
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

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public Optional<Gender> getGender() {
        return null;
    }

    @Override
    public Optional<User> getLogin() {
        return Optional.ofNullable(person instanceof User ? (User) person : null);
    }

    @Override
    public Optional<String> getEmail() {
        return null;
    }

    @Override
    public Optional<Parent> asParent() {
        return null;
    }

    @Override
    public Optional<Listener> asListener() {
        return null;
    }

    @Override
    public Optional<Clubber> asClubber() {
        return null;
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return null;
    }

    @Override
    public Optional<Family> getFamily() {
        return null;
    }

    public void setClubGroup(ClubGroup clubGroup) {
        this.clubGroup = clubGroup;
    }
}
