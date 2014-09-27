package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.program.adapter.MasterCurriculum;
import org.junit.Test;

import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProgramAdapterTest {
    @Test
    public void noClubsToStartWith() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, null, null);
        assertTrue(classUnderTest.getClubs().isEmpty());
    }

    @Test
    public void orgName() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, "An org name", null);
        assertEquals("An org name", classUnderTest.getShortName());
    }

    @Test
    public void locale() {
        ProgramAdapter classUnderTest = new ProgramAdapter("en_GB", null, null);
        assertEquals(Locale.forLanguageTag("en_GB"), classUnderTest.getLocale());
    }

    @Test
    public void curriculum() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, null, "AWANA");
        assertEquals(Programs.AWANA.get(), classUnderTest.getCurriculum());
    }

    @Test
    public void addTest() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, null, null);
        MasterCurriculum curriculum = new MasterCurriculum("foo", Collections.emptyList());
        classUnderTest.addClub(curriculum);
        assertFalse(classUnderTest.getClubs().isEmpty());
        assertEquals("foo", classUnderTest.getClubs().iterator().next().getShortName());
    }
}