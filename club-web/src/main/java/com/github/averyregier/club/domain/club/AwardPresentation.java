package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.utility.Named;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by avery on 10/12/14.
 */
public interface AwardPresentation {
    Person to();
    Named forAccomplishment();
    LocalDate earnedOn();
    Ceremony presentedAt();
    Optional<Catalogued> token();
}
