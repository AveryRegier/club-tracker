package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.domain.utility.Setting;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/5/2014.
 */
public interface Club extends ClubGroup, Comparable<Club>, Named {
    Optional<Program> asProgram();

    String getShortCode();

    ClubLeader assign(Person person, ClubLeader.LeadershipRole role);

    Curriculum getCurriculum();

    Map<Clubber, Object> getClubNightReport();

    default Collection<AwardPresentation> getAwardsNotYetPresented(AccomplishmentLevel type) {
        return getClubbers().stream()
                .flatMap(c -> c.getAwards().stream())
                .filter(AwardPresentation::notPresented)
                .filter(a -> a.getLevel() == type)
                .collect(Collectors.toList());
    }

    default List<Book> getCurrentBookList(AgeGroup currentAgeGroup) {
        return findPolicies(Policy::getBookListPolicy, () -> (ag, settings) -> getCurriculum().recommendedBookList(ag))
                .flatMap(fn -> fn.apply(currentAgeGroup, getSettings()).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    default Map<String, Setting.Type<?>> createSettingDefinitions() {
        return getCurriculum().getAgeGroups().stream()
                .map(ageGroup1 -> ageGroup1.name() + "-book")
                .collect(Collectors.toMap(
                        Function.identity(),
                        x -> Curriculum.Type.get()));
    }
}
