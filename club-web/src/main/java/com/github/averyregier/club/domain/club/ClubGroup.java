package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;

import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface ClubGroup extends Group, HasId {
    public Set<Listener> getListeners();
    public Listener recruit(Person person);

    public Optional<ClubGroup> getParentGroup();
    public Program getProgram();
    public Optional<Club> asClub();
}
