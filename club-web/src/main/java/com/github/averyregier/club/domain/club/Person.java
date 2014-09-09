package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;

import java.util.Optional;

/**
 * Created by rx39789 on 9/5/2014.
 */
public interface Person {


    public enum Gender {
        MALE,
        FEMALE;
    }
    String getId();
    public Name getName();
    public Optional<Gender> getGender();

    public Optional<User> getLogin();

    public Optional<Parent> asParent();
    public Optional<Listener> asListener();
    public Optional<Clubber> asClubber();
    public Optional<ClubLeader> asClubLeader();
}
