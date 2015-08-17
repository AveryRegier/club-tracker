package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.ClubGroup;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.Program;

import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
public class ClubLeaderAdapter extends ClubMemberAdapter implements ClubLeader {

    private final LeadershipRole leadershipRole;

    public ClubLeaderAdapter(Person person, LeadershipRole leadershipRole, ClubAdapter club) {
        super(person);
        this.leadershipRole = leadershipRole;
        person.getUpdater().setLeader(this);
        setClub(club);
    }

    @Override
    public Program getProgram() {
        return getClub().map(ClubGroup::getProgram).orElse(null);
    }

    @Override
    public LeadershipRole getLeadershipRole() {
        return leadershipRole;
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.of(this);
    }

}
