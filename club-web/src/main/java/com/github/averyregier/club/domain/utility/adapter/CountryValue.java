package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.InputField;

import java.util.Arrays;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountryValue implements InputField.Value, Comparable<CountryValue>{

    private final Locale l;
    private final Locale aDefault;

    public CountryValue(Locale l, Locale aDefault) {
        this.l = l;
        this.aDefault = aDefault;
    }

    @Override
    public String getDisplayName() {
        return l.getDisplayCountry(l);
    }

    @Override
    public String getValue() {
        return l.getCountry();
    }

    @Override
    public boolean isDefault() {
        return aDefault.getCountry().equals(l.getCountry());
    }

    @Override
    public int compareTo(CountryValue o) {
        return getDisplayName().compareTo(o.getDisplayName());
    }

    public static Stream<CountryValue> getAllCountryDropDowns(final Locale aDefault) {
        return getCountryLocales(aDefault.getLanguage())
                .filter(l -> !"".equals(l.getCountry()))
//                .filter(l -> l.getISO3Language().equals(Locale.ENGLISH.getISO3Language()))
                .distinct()
                .map(l -> new CountryValue(l, aDefault))
                .collect(Collectors.toCollection(TreeSet::new))
                .stream();
    }

    public static CountryValue findCountry(String country) {
        return getAllCountryDropDowns(Locale.getDefault())
                .filter(c -> c.getValue().equals(country))
                .findFirst()
                .orElse(null);
    }

    private static Stream<Locale> getCountryLocales(String lang) {
        return Arrays.asList(Locale.getISOCountries()).stream()
                .map(country -> new Locale(lang, country));
    }
}