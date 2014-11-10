package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface Program extends Club {
    Set<Club> getClubs();
    RegistrationInformation createRegistrationForm(User user);
    RegistrationInformation updateRegistrationForm(Map<String, String> values);
    Locale getLocale();

    void addClub(Curriculum series);

    void setName(String organizationName);


}