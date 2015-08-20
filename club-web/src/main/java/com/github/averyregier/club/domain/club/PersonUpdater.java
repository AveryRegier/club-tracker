package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.program.AgeGroup;

import java.util.Optional;

/**
 * Created by avery on 12/28/14.
 */
public interface PersonUpdater {
    void setParent(Parent thisParent);

    void setName(Name name);

    void setLeader(ClubLeader leader);

    void setListener(Listener listener);

    void setClubber(Clubber clubber);

    void setFamily(Family family);

    void setAgeGroup(AgeGroup ageGroup);

    void setEmail(String emailAddress);

    void setGender(Person.Gender gender);

    Optional<Clubber> asClubberNow();

    Person asPerson();

    boolean knowsFamily();

    void setLogin(User user);

    void setAddress(Address address);
}
