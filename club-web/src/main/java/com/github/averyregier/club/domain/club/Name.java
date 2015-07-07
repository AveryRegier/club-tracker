package com.github.averyregier.club.domain.club;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by avery on 9/5/2014.
 */
public interface Name {
    public default String getGivenName() {
        return "";
    }

    public default String getSurname() {
        return "";
    }

    public default List<String> getMiddleNames() {
        return Collections.emptyList();
    }

    public default Optional<String> getTitle() {
        return Optional.empty();
    }

    public default String getFriendlyName() {
        return "";
    }

    public default String getHonorificName() {
        return "";
    }

    public default String getFullName() {
        return (getGivenName() + " " + getSurname()).trim();
    }

}
