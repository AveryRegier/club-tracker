package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AgeGroup;

import java.time.LocalDate;
import java.util.Optional;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;

/**
 * Created by avery on 9/5/2014.
 */
public interface ClubMember extends Person {
    LocalDate getBirthDate();
    int getAge();
    AgeGroup getCurrentAgeGroup();
    Optional<Club> getClub();

    default Optional<String> getParentClubId() {
        return chain(getClub(), Club::getParentGroup).map(ClubGroup::getId);
    }

    default Optional<String> getClubId() {
        return getClub().map(Club::getId);
    }

    default boolean isInSameClub(Optional<? extends ClubMember> member) {
        return member
                .map(l -> getClubId().map(id -> id.equals(l.getClubId().orElse(null))).orElse(false))
                .orElse(false);
    }

    default boolean isListenerInSameClub(Person person) {
        return isInSameClub(person.asListener());
    }

    default boolean isInAncestorClub(Optional<? extends ClubMember> member) {
        Optional<Club> start = optMap(member, ClubMember::getClub);
        return getParentClubId()
                .map(parentClubId -> stream(start, ClubGroup::getParentGroup)
                        .anyMatch(g -> parentClubId.equals(g.getId())))
                .orElse(false);
    }

    default boolean isLeaderInSameClub(Person person) {
        return isInAncestorClub(person.asClubLeader());
    }
}
