package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.Named;

import java.time.LocalDate;

/**
 * Created by avery on 10/13/14.
 */
public interface Ceremony extends Named, HasId {
    LocalDate presentationDate();
}
