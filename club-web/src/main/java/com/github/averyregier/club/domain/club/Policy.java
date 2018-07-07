package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.Setting;
import com.github.averyregier.club.domain.utility.Settings;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
            return Optional.of((ageGroup, settings)->{
                Optional<Curriculum> first = settings.getSettings().stream()
//                        .filter(s -> s.getType().getClass() == Curriculum.class)
                        .filter(s -> s.getKey().endsWith("-book"))
                        .filter(s -> s.getKey().startsWith(ageGroup.name()))
                        .map(Setting::getValue)
                        .findFirst()
                        .map(o->((Curriculum)o));
                return first.map(Curriculum::getBooks).orElse(Collections.emptyList());
            });
        }
    };

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
}
