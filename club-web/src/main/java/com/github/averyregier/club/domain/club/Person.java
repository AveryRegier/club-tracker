package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;

import java.util.Optional;

/**
 * Created by avery on 9/5/2014.
 */
public interface Person {


    public enum Gender {
        MALE {
            @Override
            public Gender opposite() {
                return FEMALE;
            }
        },
        FEMALE {
            @Override
            public Gender opposite() {
                return MALE;
            }
        };

        public abstract Gender opposite();

        public static Optional<Gender> lookup(String s) {
            if(s == null) return Optional.empty();
            try {
                Gender gender = valueOf(s);
                return Optional.ofNullable(gender);
            } catch(IllegalArgumentException e) {
                return Optional.empty();
            }
        }
    }
    String getId();
    public Name getName();
    public Optional<Gender> getGender();
    public Optional<User> getLogin();
    public Optional<String> getEmail();

    public Optional<Parent> asParent();
    public Optional<Listener> asListener();
    public Optional<Clubber> asClubber();
    public Optional<ClubLeader> asClubLeader();

    public Optional<Family> getFamily();
}
