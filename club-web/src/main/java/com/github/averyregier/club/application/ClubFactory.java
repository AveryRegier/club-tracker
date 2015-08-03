package com.github.averyregier.club.application;

import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.Program;

import java.util.Collection;

/**
 * Created by avery on 8/2/15.
 */
public interface ClubFactory {
    UserManager getUserManager();

    Program setupProgram(String organizationName, String curriculum, String acceptLanguage);

    Program getProgram(String id);

    Connector getConnector();

    PersonManager getPersonManager();

    boolean hasPrograms();

    ClubManager getClubManager();

    Collection<Program> getPrograms(User user);
}
