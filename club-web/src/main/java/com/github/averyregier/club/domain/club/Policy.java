package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.Schedule;
import com.github.averyregier.club.domain.utility.Setting;
import com.github.averyregier.club.domain.utility.Settings;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.chain;
import static com.github.averyregier.club.domain.utility.UtilityMethods.concat;

public enum Policy {
    noSectionAwards {
        @Override
        public Optional<Predicate<AccomplishmentLevel>> getAwardsPolicy() {
            return Optional.of(AccomplishmentLevel::isBook);
        }
    },
    listenerGroupsByGender {
        @Override
        public Optional<BiPredicate<Listener, Clubber>> getListenerGroupPolicy() {
            return Optional.of(Person::gendersMatch);
        }
    },
    customizedBookSelections {
        @Override
        public Optional<BiFunction<AgeGroup, Settings, List<Book>>> getBookListPolicy() {
            return Optional.of((ageGroup, settings) -> {
                Optional<Curriculum> first = getCurriculum(ageGroup, settings);
                return first.map(Curriculum::getBooks).orElse(Collections.emptyList());
            });
        }

        @Override
        public Optional<Function<Settings, Set<Curriculum>>> getCurriculumSets() {
            return Optional.of((settings) -> settings.getSettings().stream()
                    .filter(s -> s.getKey().endsWith("-book"))
                    .map(Setting::getValue)
                    .map(o -> ((Curriculum) o))
                    .collect(Collectors.toSet()));
        }
    },
    allTogether {
        @Override
        public Optional<Boolean> isScheduled() {
            return Optional.of(Boolean.TRUE);
        }

        @Override
        public Optional<Function<Clubber, Stream<Section>>> getNextSectionPolicy() {
            return Optional.of((clubber) ->
                    chain(clubber.getClub(), club -> getCurriculumFor(clubber, club)
                            .map(curriculum -> concat(
                                    getRequiredForStarting(curriculum),
                                    getScheduledSections(club, curriculum))))
                            .orElse(Stream.empty()));
        }

        public Optional<Curriculum> getCurriculumFor(Clubber clubber, Club club) {
            return Policy.getCurriculum(clubber.getCurrentAgeGroup(), club.getSettings());
        }

        public Stream<Section> getScheduledSections(Club club, Curriculum curriculum) {
            return club.getSchedule(curriculum).map(Schedule::getAvailableEvents).orElse(Stream.empty());
        }

        private Stream<Section> getRequiredForStarting(Curriculum c) {
            return c.getBooks().stream()
                    .flatMap(b -> b.getSections().stream())
                    .filter(section -> section.getSectionType().requiredForStart());
        }
    };

    public static Optional<Curriculum> getCurriculum(AgeGroup ageGroup, Settings settings) {
        return settings.getSettings().stream()
//                        .filter(s -> s.getType().getClass() == Curriculum.class)
                .filter(s -> s.getKey().endsWith("-book"))
                .filter(s -> s.getKey().startsWith(ageGroup.name()))
                .map(Setting::getValue)
                .findFirst()
                .map(o -> ((Curriculum) o));
    }

    public static <T> Stream<T> findPolicies(Collection<Policy> policies, Function<Policy, Optional<T>> policy) {
        return policies.stream().map(policy)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Optional<Predicate<AccomplishmentLevel>> getAwardsPolicy() {
        return Optional.empty();
    }

    public Optional<BiPredicate<Listener, Clubber>> getListenerGroupPolicy() {
        return Optional.empty();
    }

    public Optional<BiFunction<AgeGroup, Settings, List<Book>>> getBookListPolicy() {
        return Optional.empty();
    }

    public Optional<Function<Settings, Set<Curriculum>>> getCurriculumSets() {
        return Optional.empty();
    }

    public Optional<Function<Clubber, Stream<Section>>> getNextSectionPolicy() {
        return Optional.empty();
    }

    public Optional<Boolean> isScheduled() {
        return Optional.empty();
    }
}
