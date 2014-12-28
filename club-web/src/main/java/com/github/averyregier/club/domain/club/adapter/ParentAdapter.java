package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
class ParentAdapter implements Parent {
    private final Person user;
    public Family family;

    public ParentAdapter(Person person) {
        this.user = person;
    }

    @Override
    public String getId() {
        return user.getId();
    }

    @Override
    public Name getName() {
        return user.getName();
    }

    @Override
    public Optional<Gender> getGender() {
        return user.getGender();
    }

    @Override
    public Optional<User> getLogin() {
        return user.getLogin();
    }

    @Override
    public Optional<String> getEmail() {
        return user.getEmail();
    }

    @Override
    public Optional<Parent> asParent() {
        return Optional.of(this);
    }

    @Override
    public Optional<Listener> asListener() {
        return user.asListener();
    }

    @Override
    public Optional<Clubber> asClubber() {
        return user.asClubber();
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return user.asClubLeader();
    }

    @Override
    public Optional<Family> getFamily() {
        return Optional.ofNullable(family);
    }

    protected void setFamily(Family family) {
        this.family = family;
    }
}
