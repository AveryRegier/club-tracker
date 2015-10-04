package com.github.averyregier.club.domain.club;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.averyregier.club.domain.utility.UtilityMethods.killWhitespace;
import static com.github.averyregier.club.domain.utility.UtilityMethods.orEmpty;

/**
 * Created by avery on 9/5/2014.
 */
public interface Name extends Comparable<Name> {
    default String getGivenName() {
        return "";
    }

    default String getSurname() {
        return "";
    }

    default List<String> getMiddleNames() {
        return Collections.emptyList();
    }

    default Optional<String> getTitle() {
        return Optional.empty();
    }

    default String getFriendlyName() {
        return "";
    }

    default String getHonorificName() {
        return "";
    }

    default String getFullName() {
        return (getGivenName() + " " + getSurname()).trim();
    }

    default boolean isEmpty() {
        return killWhitespace(getFullName()) == null;
    }

    default int compareTo(Name b) {
        if(b == null) return 1;
        String as = orEmpty(getSurname());
        String bs = orEmpty(b.getSurname());
        int result = as.compareTo(bs);
        if(result != 0) return result;
        String ag = orEmpty(getGivenName());
        String bg = orEmpty(b.getGivenName());
        return ag.compareTo(bg);
    }
}
