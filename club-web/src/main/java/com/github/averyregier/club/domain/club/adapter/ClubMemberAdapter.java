package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubMember;
import com.github.averyregier.club.domain.club.Person;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by avery on 12/28/14.
 */
public class ClubMemberAdapter extends PersonWrapper implements ClubMember {
    private Club club;
    private final Person person;

    public ClubMemberAdapter(Person person) {
        this.person = person;
    }

    @Override
    public LocalDate getBirthDate() {
        return null;
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public Optional<Club> getClub() {
        return Optional.ofNullable(club);
    }

    @Override
    protected Person getPerson() {
        return person;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
