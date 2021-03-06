package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.utility.DisplayNamed;
import com.github.averyregier.club.domain.utility.HasId;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by avery on 10/12/14.
 */
public interface AwardPresentation extends HasId {
    Person to();
    DisplayNamed forAccomplishment();
    LocalDate earnedOn();
    Ceremony presentedAt();
    Optional<Catalogued> token();
    ClubberRecord record();

    void presentAt(Ceremony ceremony);

    void undoPresentation();

    boolean notPresented();

    AccomplishmentLevel getLevel();
}
