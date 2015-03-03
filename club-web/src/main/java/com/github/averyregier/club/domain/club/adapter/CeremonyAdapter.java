package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Ceremony;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;

/**
* Created by avery on 1/17/15.
*/
public class CeremonyAdapter implements Ceremony {
    private String id = UUID.randomUUID().toString();
    private final LocalDate presentationDate;

    public CeremonyAdapter(LocalDate presentationDate) {
        this.presentationDate = presentationDate;
    }

    public CeremonyAdapter() {
        this(LocalDate.now());
    }

    @Override
    public String getName() {
        return presentationDate != null ?
                presentationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) :
                null;
    }

    @Override
    public LocalDate presentationDate() {
        return presentationDate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getShortCode() {
        return getName();
    }
}
