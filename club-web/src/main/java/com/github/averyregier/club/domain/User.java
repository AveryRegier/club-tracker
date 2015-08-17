package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubMember;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.NameBuilder;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonWrapper;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.view.UserBean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;

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

    public User(Person person) {
        if(person != null) {
            this.person = person.getUpdater().asPerson();
            this.person.getUpdater().setLogin(this);
        }
    }

    public User(Person personAdapter, Integer initialAuth) {
        this.person = personAdapter;
        this.auth = initialAuth;
        this.person.getUpdater().setLogin(this);
    }

    public Login getLoginInformation() {
        return new Login();
    }

    boolean resetAuthIfNeeded() {
        if(auth == null) {
            resetAuth();
            return true;
        }
        return false;
    }

    public Collection<Club> getClubs() {
        HashSet<Club> clubs = new HashSet<>();
        addPersonalClubs(this, clubs);
        asParent().ifPresent(
                parent->parent.getFamily().ifPresent(
                        f -> {
                            f.getClubbers().stream()
                                    .forEach(addClub(clubs));
                            getOther(f.getParents(), parent).ifPresent(
                                    spouse -> addPersonalClubs(spouse, clubs));
                        }));
        return clubs;
    }

    private static void addPersonalClubs(Person person, HashSet<Club> clubs) {
        person.asClubber().ifPresent(addClub(clubs));
        person.asClubLeader().ifPresent(addClub(clubs));
        person.asListener().ifPresent(addClub(clubs));
    }

    private static Consumer<ClubMember> addClub(HashSet<Club> clubs) {
        return member->member.getClub().ifPresent(clubs::add);
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
        return updateLogin(user) |
               updateIdentity(user);
    }

    public boolean updateIdentity(final UserBean user) {
        return updateName(user) |
               updateGender(user.getGender()) |
               updateEmail(user.getEmail());
    }

    private boolean updateEmail(String email) {
        return !UtilityMethods.isEmpty(email) &&
               change(getEmail().orElse(null),
                       email,
                       (e) -> getUpdater().setEmail(e));
    }

    private boolean updateGender(String gender1) {
        return !UtilityMethods.isEmpty(gender1) &&
               change(getGender().orElse(null),
                       Gender.valueOf(gender1.toUpperCase()),
                       (g) -> getUpdater().setGender(g));
    }

    private boolean updateName(UserBean user) {
        NameBuilder nameBuilder = new NameBuilder(getName(), false)
                .given(user.getFirstName())
                .surname(user.getLastName());
        person.getUpdater().setName(nameBuilder.build());
        return nameBuilder.isChanged();
    }

    public boolean updateLogin(UserBean user) {
        return change(this.id, user.getUniqueId(), v -> this.id = v) |
               change(this.providerID, user.getProviderId(), v -> this.providerID = v);
    }

}
