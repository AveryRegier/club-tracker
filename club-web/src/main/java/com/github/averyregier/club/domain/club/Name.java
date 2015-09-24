package com.github.averyregier.club.domain.club;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.averyregier.club.domain.utility.UtilityMethods.killWhitespace;

/**
 * Created by avery on 9/5/2014.
 */
public interface Name {
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
}
