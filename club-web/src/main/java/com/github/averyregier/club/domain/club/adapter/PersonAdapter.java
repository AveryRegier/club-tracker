package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
public class PersonAdapter implements Person {
    private Name name;
    private Gender gender;
    private String email;
    private Listener listener;
    private ClubLeader leader;
    private Parent parent;

    @Override
    public String getId() {
        return null;
    }

    public Name getName() {
        return name;
    }

    @Override
    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }

    @Override
    public Optional<User> getLogin() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    @Override
    public Optional<Parent> asParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public Optional<Listener> asListener() {
        return Optional.ofNullable(listener);
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.ofNullable(leader);
    }

    @Override
    public Optional<Family> getFamily() {
        return asParent().map(Person::getFamily).orElse(asClubber().map(Person::getFamily).orElse(Optional.empty()));
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.empty();
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setLeader(ClubLeader leader){
        this.leader = leader;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
