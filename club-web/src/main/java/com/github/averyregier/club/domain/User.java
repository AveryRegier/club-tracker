package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonWrapper;
import com.github.averyregier.club.view.UserBean;

import java.io.UnsupportedEncodingException;
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
    private Integer auth;
    private String id;
    private String providerID;

    public User() {
        this(new PersonAdapter());
    }

    public User(Person personAdapter) {
        this.person = personAdapter;
    }

    public User(Person personAdapter, Integer initialAuth) {
        this.person = personAdapter;
        this.auth = initialAuth;
    }

    public Login getLoginInformation() {
        return new Login();
    }

    public class Login {

        public Optional<Integer> getAuth() {
            return Optional.ofNullable(auth);
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

        @Override
        public boolean equals(Object obj) {
            if(obj != null && obj instanceof Login) {
                Login other = (Login) obj;
                return safeEquals(getAuth(), other.getAuth()) &&
                        safeEquals(getID(), other.getID()) &&
                        safeEquals(getProviderID(), other.getProviderID()) &&
                        safeEquals(getUniqueID(), other.getUniqueID());
            }
            return false;
        }

        @Override
        public String toString() {
            return getProviderID()+","+getID()+","+getUniqueID()+","+getAuth();
        }
    }

    private boolean safeEquals(Object a, Object b) {
        if(a == b) return true;
        if(a == null) return false;
        if(b == null) return false;
        return a.equals(b);
    }

    private static int toInt(String auth) {
        return Integer.parseInt(auth, Character.MAX_RADIX);
    }

    private static String fromInt(Integer auth) {
        return Integer.toString(auth, Character.MAX_RADIX);
    }

    private static String decodeAuth(String auth) {
        try {
            return URLDecoder.decode(auth, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return auth;
        }
    }

    public String resetAuth() {
        auth = new Random(System.currentTimeMillis()).nextInt();
        return encodeAuth(fromInt(auth));
    }

    private static String encodeAuth(String auth) {
        if(auth != null) {
            try {
                auth = URLEncoder.encode(auth, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return auth;
    }

    @Override
    protected Person getPerson() {
        return person;
    }

    public boolean authenticate(String auth) {
        boolean allThere = auth != null && this.auth != null;
        if (allThere) {
            int attempted = toInt(decodeAuth(auth));
            return attempted == this.auth;
        }
        return false;
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
