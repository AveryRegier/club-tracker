package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.Setting;
import com.github.averyregier.club.domain.utility.Settings;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PolicyHolder extends HasId {
    default <T> Stream<T> findPolicies(Function<Policy, Optional<T>> policy) {
        return Policy.findPolicies(getPolicies(), policy);
    }

    default <T> Stream<T> findPolicies(Function<Policy, Optional<T>> policy, Supplier<T> defaultPolicy) {
        List<T> list = Policy.findPolicies(getPolicies(), policy).collect(Collectors.toList());
        return list.isEmpty() ? Stream.of(defaultPolicy.get()) : list.stream();
    }

    Collection<Policy> getPolicies();

    void replacePolicies(Collection<Policy> policies, Settings settings);

    Settings getSettings();

    default Map<String, Setting.Type<?>> createSettingDefinitions() {
        return Collections.emptyMap();
    }
}
