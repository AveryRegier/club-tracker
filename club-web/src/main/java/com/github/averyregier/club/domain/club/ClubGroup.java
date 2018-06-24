package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by avery on 9/6/2014.
 */
public interface ClubGroup extends Group, HasId {
    Set<Listener> getListeners();
    Listener recruit(Person person);

    Optional<ClubGroup> getParentGroup();
    Program getProgram();
    Optional<Club> asClub();

    default <T> Stream<T> findPolicy(Function<Policy, Optional<T>> policy) {
        return Policy.findPolicies(getPolicies(), policy);
    }

    void addPolicy(Policy policy);
    Collection<Policy> getPolicies();
}
