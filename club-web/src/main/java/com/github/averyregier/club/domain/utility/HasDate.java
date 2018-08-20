package com.github.averyregier.club.domain.utility;

import java.time.LocalDate;

public interface HasDate extends HasId {
    LocalDate getDate();
}
