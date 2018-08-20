package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasTimezone;
import com.github.averyregier.club.domain.utility.Schedule;

public interface ClubYear extends HasTimezone {
    String getClubYear();

    Schedule<ClubYear, ClubMeeting> getSchedule();

    Club getClub();
}
