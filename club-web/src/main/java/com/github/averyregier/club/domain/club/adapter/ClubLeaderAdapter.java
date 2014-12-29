package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.Program;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
class ClubLeaderAdapter extends ClubMemberAdapter implements ClubLeader {

    public ClubLeaderAdapter(Person person) {
        super(person);
        person.getUpdater().setLeader(this);
    }

    @Override
    public Program getProgram() {
        return getClub().map(c->c.getProgram()).orElse(null);
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.of(this);
    }

}
