package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Name;

import java.util.List;
import java.util.Optional;

/**
 * Created by avery on 7/6/15.
 */
class ImmutableName implements Name {
    private final String givenValue;
    private final String surnameValue;
    private final String friendlyValue;
    private final Optional<String> titleValue;
    private final String honorificValue;
    private final List<String> middleValue;

    public ImmutableName(String givenValue, String surnameValue, String friendlyValue, Optional<String> titleValue, String honorificValue, List<String> middleValue) {
        this.givenValue = givenValue;
        this.surnameValue = surnameValue;
        this.friendlyValue = friendlyValue;
        this.titleValue = titleValue;
        this.honorificValue = honorificValue;
        this.middleValue = middleValue;
    }

    @Override
    public String getGivenName() {
        return givenValue;
    }

    @Override
    public String getSurname() {
        return surnameValue;
    }

    @Override
    public String getFriendlyName() {
        return friendlyValue;
    }

    @Override
    public Optional<String> getTitle() {
        return titleValue;
    }

    @Override
    public String getHonorificName() {
        return honorificValue;
    }

    @Override
    public List<String> getMiddleNames() {
        return middleValue;
    }

    @Override
    // freemarker doesn't like default methods
    public String getFullName() {
        return (getGivenName() + " " + getSurname()).trim();
    }
}
