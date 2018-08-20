package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.Schedule;

public interface TeachingPlan {
    Curriculum getCurriculum();
    Schedule<Club, Section> getSchedule();
    ClubYear getYear();
}
