package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Person;

/**
 * Created by avery on 2/28/15.
 */
public class MockLeader extends ClubLeaderAdapter {
    public MockLeader(Person person, LeadershipRole leadershipRole, ClubAdapter club) {
        super(person, leadershipRole, club);
    }
}
