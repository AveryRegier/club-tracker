package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum Policy {
    noSectionAwards {
        @Override
        public Optional<Predicate<AccomplishmentLevel>> getAwardsPolicy() {
            return Optional.of(AccomplishmentLevel::isBook);
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
}
