package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
public class PersonAdapter implements Person, PersonUpdater {
    private String uuid;
    private Name name;
    private Gender gender;
    private String email;
    private Listener listener;
    private ClubLeader leader;
    private Parent parent;
    private Clubber clubber;
    private Family family;
    private AgeGroup currentAgeGroup;

    public PersonAdapter() {
        this.uuid = null;
    }

    public PersonAdapter(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getId() {
        return uuid;
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

    protected boolean knowsFamily() {
        return parent != null || clubber != null || family != null;
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
        return Optional.ofNullable(family);
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.ofNullable(clubber);
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void setClubber(Clubber clubber) {
        this.clubber = clubber;
    }

    @Override
    public void setFamily(Family family) {
        this.family = family;
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

    @Override
    public PersonUpdater getUpdater() {
        return this;
    }

    public AgeGroup getCurrentAgeGroup() {
        return currentAgeGroup;
    }

    @Override
    public void setAgeGroup(AgeGroup ageGroup) {
        this.currentAgeGroup = ageGroup;
    }
}
