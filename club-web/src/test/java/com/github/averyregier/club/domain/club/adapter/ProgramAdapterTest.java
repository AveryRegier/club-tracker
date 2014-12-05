package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.program.adapter.MasterCurriculum;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import org.junit.Test;

import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.*;

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
    public void addClub() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, null, null);
        MasterCurriculum curriculum = new MasterCurriculum("foo", Collections.emptyList());
        classUnderTest.addClub(curriculum);
        assertFalse(classUnderTest.getClubs().isEmpty());
        assertEquals("foo", classUnderTest.getClubs().iterator().next().getShortName());
    }

    @Test
    public void registerClubber() {
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        program.addClub(TnTCurriculum.get());

        ClubberAdapter clubber = new ClubberAdapter() {
            @Override
            public AgeGroup getCurrentAgeGroup() {
                return AgeGroup.DefaultAgeGroup.THIRD_GRADE;
            }
        };
        program.register(clubber);

        assertTrue(program.getClubbers().contains(clubber));
        assertTrue(clubber.getClub().isPresent());
        Club clubbersClub = clubber.getClub().get();
        assertTrue(program.getClubs().contains(clubbersClub));
        assertEquals("TnT", clubbersClub.getShortName());
    }

    @Test
    public void registerClubberFails() {
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        program.addClub(TnTCurriculum.get());

        ClubberAdapter clubber = new ClubberAdapter() {
            @Override
            public AgeGroup getCurrentAgeGroup() {
                return AgeGroup.DefaultAgeGroup.COLLEGE;
            }
        };
        program.register(clubber);

        assertFalse(program.getClubbers().contains(clubber));
        assertFalse(clubber.getClub().isPresent());
    }
}