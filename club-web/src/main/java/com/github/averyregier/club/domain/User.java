package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.NameBuilder;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonWrapper;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.view.UserBean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.Random;

import static com.github.averyregier.club.domain.utility.UtilityMethods.change;
import static com.github.averyregier.club.domain.utility.UtilityMethods.safeEquals;

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
        return getCurrentAuth();
    }

    public String getCurrentAuth() {
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
        person.getUpdater().setName(new NameBuilder()
                .given(first != null ? first.toString() : "")
                .surname(last != null ? last.toString() : "")
                .build());
    }

    public boolean update(final UserBean user) {
        boolean changed =
                change(this.id, user.getUniqueId(), v -> this.id = v) |
                change(this.providerID, user.getProviderId(), v -> this.providerID = v);

        NameBuilder nameBuilder = new NameBuilder(getName(), false)
                .given(user.getFirstName())
                .surname(user.getLastName());
        changed = changed | nameBuilder.isChanged();
        person.getUpdater().setName(nameBuilder.build());

        String gender1 = user.getGender();
        if(!UtilityMethods.isEmpty(gender1)) {
            changed = changed |
                    change(getGender().orElse(null),
                            Gender.valueOf(gender1.toUpperCase()),
                            (g) -> getUpdater().setGender(g));
        }
        String email = user.getEmail();
        if(!UtilityMethods.isEmpty(email)) {
            person.getUpdater().setEmail(email);
            changed = changed |
                    change(getEmail().orElse(null),
                            email,
                            (e) -> getUpdater().setEmail(e));
        }
        return changed;
    }

}
