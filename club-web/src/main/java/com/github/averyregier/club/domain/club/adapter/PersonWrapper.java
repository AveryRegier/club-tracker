package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;

import java.util.Optional;

/**
 * Created by avery on 12/28/14.
 */
public abstract class PersonWrapper implements Person {
    protected abstract Person getPerson();

    @Override
    public String getId() {
        return getPerson().getId();
    }

    @Override
    public Name getName() {
        return getPerson().getName();
    }

    @Override
    public Optional<Gender> getGender() {
        return getPerson().getGender();
    }

    @Override
    public Optional<User> getLogin() {
        return getPerson().getLogin();
    }

    @Override
    public Optional<String> getEmail() {
        return getPerson().getEmail();
    }

    @Override
    public Optional<Parent> asParent() {
        return getPerson().asParent();
    }

    @Override
    public Optional<Listener> asListener() {
        return getPerson().asListener();
    }

    @Override
    public Optional<Clubber> asClubber() {
        return getPerson().asClubber();
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return getPerson().asClubLeader();
    }

    @Override
    public Optional<Family> getFamily() {
        return getPerson().getFamily();
    }

    @Override
    public PersonUpdater getUpdater() {
        return getPerson().getUpdater();
    }

    public AgeGroup getCurrentAgeGroup() {
        return ((PersonAdapter)getPerson()).getCurrentAgeGroup();
    }
}