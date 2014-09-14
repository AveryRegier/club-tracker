package com.github.averyregier.club.domain.club;

import java.util.Locale;
import java.util.Set;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Program extends Club {
    public Set<Club> getClubs();
    public RegistrationInformation createRegistrationForm();
    public Locale getLocale();
}
