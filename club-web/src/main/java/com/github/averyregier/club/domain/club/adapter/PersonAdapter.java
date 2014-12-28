package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
class PersonAdapter implements Person {
    private final Name name;

    public PersonAdapter(Name name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Optional<Gender> getGender() {
        return Optional.empty();
    }

    @Override
    public Optional<User> getLogin() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getEmail() {
        return Optional.empty();
    }

    @Override
    public Optional<Parent> asParent() {
        return Optional.empty();
    }

    @Override
    public Optional<Listener> asListener() {
        return Optional.empty();
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.empty();
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.empty();
    }

    @Override
    public Optional<Family> getFamily() {
        return Optional.empty();
    }
}
