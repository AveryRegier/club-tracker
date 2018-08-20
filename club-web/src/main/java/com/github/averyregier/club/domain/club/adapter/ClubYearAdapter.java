package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubMeeting;
import com.github.averyregier.club.domain.club.ClubYear;
import com.github.averyregier.club.domain.utility.Schedule;
import com.github.averyregier.club.domain.utility.Scheduled;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClubYearAdapter implements ClubYear {
    private Club club;
    private String uuid = UUID.randomUUID().toString();
    private final String clubYear;
    private Schedule<ClubYear, ClubMeeting> schedule;

    public ClubYearAdapter(Club club, String clubYear, List<LocalDate> dates) {
        this.club = club;
        this.clubYear = clubYear;
        this.schedule = new Schedule<>(this, generate(dates));
    }

    private List<Scheduled<ClubYear, ClubMeeting>> generate(List<LocalDate> dates) {
        return dates.stream()
                .map(d->new Scheduled<ClubYear, ClubMeeting>(this, d, new ClubMeetingAdapter(this, d)))
                .collect(Collectors.toList());
    }

    @Override
    public ZoneId getTimeZone() {
        return club.getTimeZone();
    }

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public String getShortCode() {
        return getClubYear();
    }

    @Override
    public String getClubYear() {
        return clubYear;
    }

    @Override
    public Schedule<ClubYear, ClubMeeting> getSchedule() {
        return schedule;
    }

    @Override
    public Club getClub() {
        return club;
    }
}
