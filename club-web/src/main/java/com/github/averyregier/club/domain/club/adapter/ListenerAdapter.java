package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
* Created by avery on 12/16/14.
*/
class ListenerAdapter extends PersonWrapper implements Listener {
    private final Person person;
    private ClubGroup clubGroup;

    public ListenerAdapter(Person person) {
        this.person = person;
        person.getUpdater().setListener(this);
    }

    @Override
    public Set<Clubber> getQuickList() {
        return null;
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
    public AgeGroup getCurrentAgeGroup() {
        return null;
    }

    @Override
    public Optional<Club> getClub() {
        return clubGroup.asClub();
    }

    public void setClubGroup(ClubGroup clubGroup) {
        this.clubGroup = clubGroup;
    }

    @Override
    protected Person getPerson() {
        return person;
    }
}
