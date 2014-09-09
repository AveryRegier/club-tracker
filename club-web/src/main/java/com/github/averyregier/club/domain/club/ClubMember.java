package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AgeGroup;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by rx39789 on 9/5/2014.
 */
public interface ClubMember extends Person {
    public LocalDate getBirthDate();
    public int getAge();
    public AgeGroup getCurrentAgeGroup();
    public Optional<Club> getClub();
}
