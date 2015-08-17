package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface Program extends Club {
    Set<Club> getClubs();
    RegistrationInformation createRegistrationForm(Person user);
    RegistrationInformation createRegistrationForm();
    RegistrationInformation updateRegistrationForm(Map<String, String> values);

    Locale getLocale();

    Club addClub(Curriculum series);


    void setName(String organizationName);

    Optional<Club> lookupClub(String shortCode);
    void setPersonManager(PersonManager personManager);

    PersonManager getPersonManager();
}