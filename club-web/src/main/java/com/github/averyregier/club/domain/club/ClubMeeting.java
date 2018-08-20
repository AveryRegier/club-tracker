package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasDate;

public interface ClubMeeting extends HasDate {
    ClubYear getClubYear();
}
