package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by avery on 9/6/2014.
 */
public interface Program extends Club {
    Set<Club> getClubs();
    RegistrationInformation createRegistrationForm(Person user);
    RegistrationInformation createRegistrationForm();
    RegistrationInformation updateRegistrationForm(Map<String, String> values);

    void setName(String organizationName);
    Locale getLocale();

    Club addClub(Curriculum series);
    Optional<Club> lookupClub(String shortCode);

    void setPersonManager(PersonManager personManager);
    PersonManager getPersonManager();

    Program addField(RegistrationSection section, InputFieldDesignator field);
    Optional<InputField> findField(String fieldId);

    void setMeetings(String clubYear, List<LocalDate> dates);
}