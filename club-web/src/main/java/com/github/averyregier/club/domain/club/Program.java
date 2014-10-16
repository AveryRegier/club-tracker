package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.Locale;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface Program extends Club {
    public Set<Club> getClubs();
    public RegistrationInformation createRegistrationForm(User user);
    public Locale getLocale();

    void addClub(Curriculum series);

    void setName(String organizationName);
}
