package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Ceremony;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
* Created by avery on 1/17/15.
*/
public class CeremonyAdapter implements Ceremony {
    private final LocalDate presentationDate;

    public CeremonyAdapter(LocalDate presentationDate) {
        this.presentationDate = presentationDate;
    }

    public CeremonyAdapter() {
        this(LocalDate.now());
    }

    @Override
    public String getName() {
        return presentationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    @Override
    public LocalDate presentationDate() {
        return presentationDate;
    }
}
