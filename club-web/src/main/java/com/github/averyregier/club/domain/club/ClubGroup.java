package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasTimezone;

import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface ClubGroup extends Group, PolicyHolder, HasTimezone {
    Set<Listener> getListeners();
    Listener recruit(Person person);

    Optional<ClubGroup> getParentGroup();
    Program getProgram();
    Optional<Club> asClub();
}
