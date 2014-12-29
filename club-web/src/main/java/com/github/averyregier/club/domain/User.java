package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonWrapper;
import com.github.averyregier.club.view.UserBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by avery on 9/2/14.
 */
public class User extends PersonWrapper implements Person {
    private PersonAdapter person = new PersonAdapter();
    private String auth;
    private String id;

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

    @Override
    protected Person getPerson() {
        return person;
    }

    public boolean authenticate(String auth) {
        return auth != null && auth.equals(this.auth);
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public Optional<User> getLogin() {
        return Optional.of(this);
    }


    public void setName(Object first, Object last) {
        person.setName(new Name() {
            @Override
            public String getGivenName() {
                return first != null ? first.toString() : null;
            }

            @Override
            public String getSurname() {
                return last != null ? last.toString() : null;
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
                return combineName(first, last);
            }
        });
    }

    public void update(final UserBean user) {
        this.id = user.getUniqueId();

        setName(user.getFirstName(), user.getLastName());

        String gender1 = user.getGender();
        if(gender1 != null) {
            person.setGender(Gender.valueOf(gender1.toUpperCase()));
        }
        person.setEmail(user.getEmail());
    }

    private String combineName(Object first, Object last) {
        return ((first != null ? first : "") +" "+ (last != null ? last : "")).trim();
    }

}
