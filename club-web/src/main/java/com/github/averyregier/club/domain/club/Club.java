package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.domain.utility.Schedule;
import com.github.averyregier.club.domain.utility.Setting;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.UtilityMethods.findToday;

/**
 * Created by avery on 9/5/2014.
 */
public interface Club extends ClubGroup, Comparable<Club>, Named {
    Optional<Program> asProgram();

    String getShortCode();

    ClubLeader assign(Person person, ClubLeader.LeadershipRole role);

    Curriculum getCurriculum();

    default Map<Clubber, Object> getClubNightReport() {
        LocalDate date = findToday(Optional.of(this));
        return new TreeMap<>(getClubbers().stream()
                .collect(Collectors.toMap(Function.identity(),
                        c -> c.getRecords(
                                (r) -> r.getSigning()
                                        .map(s -> s.getDate().equals(date))
                                        .orElse(false)))));
    }


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

    default Set<Curriculum> getScheduledCurriculum() {
        return findPolicies(Policy::getCurriculumSets, () -> (settings) -> Collections.singleton(getCurriculum()))
                .flatMap(fn->fn.apply(getSettings()).stream())
                .filter(Curriculum::isScheduled)
                .collect(Collectors.toSet());
    }

    default void setSchedule(Curriculum curriculum, Schedule<Club, Section> schedule) {}

    Optional<Schedule<Club, Section>> getSchedule(Curriculum curriculum);

    default boolean isScheduled() {
        return findPolicies(Policy::isScheduled).anyMatch(x->x);
    }
}
