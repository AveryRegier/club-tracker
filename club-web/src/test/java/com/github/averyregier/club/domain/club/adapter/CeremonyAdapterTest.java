package com.github.averyregier.club.domain.club.adapter;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static org.junit.Assert.assertEquals;

public class CeremonyAdapterTest {

    @Test
    public void now() {
        CeremonyAdapter classUnderTest = new CeremonyAdapter();
        assertEquals(LocalDate.now(), classUnderTest.presentationDate());
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                classUnderTest.getName());
    }



}