package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.Named;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by avery on 9/5/2014.
 */
public interface Club extends ClubGroup, Comparable<Club>, Named {
    Optional<Program> asProgram();
    String getShortCode();
    ClubLeader assign(Person person, ClubLeader.LeadershipRole role);
    Curriculum getCurriculum();

    Collection<AwardPresentation> getAwardsNotYetPresented(AccomplishmentLevel type);
    Map<Clubber, Object> getClubNightReport();

    default List<Book> getCurrentBookList(AgeGroup currentAgeGroup) {
        return getCurriculum().recommendedBookList(currentAgeGroup);
    }
}
