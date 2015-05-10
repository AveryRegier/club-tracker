package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonWrapper;
import com.github.averyregier.club.view.UserBean;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by avery on 9/2/14.
 */
public class User extends PersonWrapper implements Person {
    private Person person;
    private String auth;
    private String id;
    private String providerID;

    public User() {
        this(new PersonAdapter());
    }

    public User(Person personAdapter) {
        this.person = personAdapter;
    }

    public Login getLoginInformation() {
        return new Login();
    }

    public class Login {

        public Optional<Integer> getAuth() {
            return auth == null ? Optional.empty() : Optional.of(toInt(decodeAuth()));
        }

        public String getID() {
            return person.getId();
        }

        public String getProviderID() {
            return providerID;
        }

        public String getUniqueID() {
            return id;
        }
    }

    private int toInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    private byte[] decodeAuth() {
        try {
            return URLDecoder.decode(auth, "UTF-8").getBytes();
        } catch (UnsupportedEncodingException e) {
            return auth.getBytes();
        }
    }

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
        return person.getId();
    }


    @Override
    public Optional<User> getLogin() {
        return Optional.of(this);
    }


    public void setName(Object first, Object last) {
        person.getUpdater().setName(new Name() {
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
        this.providerID = user.getProviderId();

        setName(user.getFirstName(), user.getLastName());

        String gender1 = user.getGender();
        if(gender1 != null) {
            person.getUpdater().setGender(Gender.valueOf(gender1.toUpperCase()));
        }
        person.getUpdater().setEmail(user.getEmail());
    }

    private String combineName(Object first, Object last) {
        return ((first != null ? first : "") +" "+ (last != null ? last : "")).trim();
    }

}
