package com.github.averyregier.club.application;

import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.*;

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

    Collection<Program> getPrograms(Person person);


    default Clubber findClubber( String id) {
        return findPerson(id)
                .asClubber()
                .orElseThrow(IllegalArgumentException::new);
    }

    default Listener findListener(String id, Club club) {
        Listener listener = findPerson(id)
                .asListener()
                .orElseThrow(IllegalArgumentException::new);
        if(listener.getClub().orElseThrow(IllegalArgumentException::new).getId().equals(club.getId())) {
            return listener;
        } else throw new IllegalArgumentException();
    }

    default Person findPerson(String id) {
        return getPersonManager()
                .lookup(id)
                .orElseThrow(IllegalArgumentException::new);
    }

    void reset();
}
