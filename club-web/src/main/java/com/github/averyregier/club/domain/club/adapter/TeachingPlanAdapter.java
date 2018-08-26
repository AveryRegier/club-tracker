package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubYear;
import com.github.averyregier.club.domain.club.TeachingPlan;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.Schedule;

public class TeachingPlanAdapter implements TeachingPlan {
    private final Curriculum curriculum;
    private final Schedule<Club, Section> schedule;
    private final ClubYear clubYear;

    public TeachingPlanAdapter(Curriculum curriculum, Schedule<Club, Section> schedule, ClubYear clubYear) {
        this.curriculum = curriculum;
        this.schedule = schedule;
        this.clubYear = clubYear;
    }

    @Override
    public Curriculum getCurriculum() {
        return curriculum;
    }

    @Override
    public Schedule<Club, Section> getSchedule() {
        return schedule;
    }

    @Override
    public ClubYear getYear() {
        return clubYear;
    }
}
