package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AgeGroup;

/**
 * Created by avery on 12/28/14.
 */
public interface PersonUpdater {
    void setParent(Parent thisParent);

    void setName(Name name);

    void setLeader(ClubLeader leader);

    void setListener(Listener listener);

    void setClubber(Clubber clubber);

    void setFamily(Family family);

    void setAgeGroup(AgeGroup ageGroup);
}
