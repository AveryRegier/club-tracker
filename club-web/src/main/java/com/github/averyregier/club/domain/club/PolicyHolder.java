package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface PolicyHolder extends HasId {
    default <T> Stream<T> findPolicy(Function<Policy, Optional<T>> policy) {
        return Policy.findPolicies(getPolicies(), policy);
    }

    Collection<Policy> getPolicies();
    void replacePolicies(Collection<Policy> policies);
}
