package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.ClubMeeting;
import com.github.averyregier.club.domain.club.ClubYear;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ClubMeetingAdapter implements ClubMeeting {
    private final ClubYear clubYear;
    private final LocalDate date;
    private final String id = UUID.randomUUID().toString();

    public ClubMeetingAdapter(ClubYear clubYear, LocalDate date) {
        this.clubYear = clubYear;
        this.date = date;
    }
    
    @Override public ClubYear getClubYear() {
        return clubYear;
    }

    @Override public LocalDate getDate() {
        return date;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getShortCode() {
        return date.format(DateTimeFormatter.ofPattern("dd-mm-yy"));
    }
}
