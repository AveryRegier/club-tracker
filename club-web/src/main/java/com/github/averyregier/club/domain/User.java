package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.view.UserBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by avery on 9/2/14.
 */
public class User implements Person {
    private String auth;
    private String name;
    private String id;
    private String first;
    private String last;
    private Gender gender;

    public String resetAuth() {
        byte[] bytes = new byte[10];
        new Random(System.currentTimeMillis()).nextBytes(bytes);
        auth = new String(bytes);
        try {
            auth = URLEncoder.encode(auth, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return auth;
    }

    public boolean authenticate(String auth) {
        return auth != null && auth.equals(this.auth);
    }

    @Override
    public String getId() {
        return id;
    }

    public Name getName() {
        return new Name() {
            @Override
            public String getGivenName() {
                return first;
            }

            @Override
            public String getSurname() {
                return last;
            }

            @Override
            public List<String> getMiddleNames() {
                return null;
            }

            @Override
            public Optional<String> getTitle() {
                return null;
            }

            @Override
            public String getFriendlyName() {
                return null;
            }

            @Override
            public String getHonorificName() {
                return null;
            }

            @Override
            public String getFullName() {
                return name;
            }
        };
    }

    @Override
    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }

    @Override
    public Optional<User> getLogin() {
        return Optional.of(this);
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

    public void setName(Object first, Object last) {
        this.first = first != null ? first.toString() : null;
        this.last = last != null ? last.toString() : null;
        this.name = combineName(first, last);
    }

    public void update(UserBean user) {
        this.id = user.getUniqueId();
        this.name = user.getName();
        String gender1 = user.getGender();
        if(gender1 != null) {
            this.gender = Gender.valueOf(gender1);
        }
    }

    private String combineName(Object first, Object last) {
        return ((first != null ? first : "") +" "+ (last != null ? last : "")).trim();
    }
}
