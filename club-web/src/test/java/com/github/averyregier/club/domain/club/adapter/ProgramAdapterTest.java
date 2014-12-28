package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.RegistrationTest;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.program.adapter.MasterCurriculum;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import org.junit.Test;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

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

    @Test
    public void recruitExistingUserAsListener() {
        User user = new User();
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");

        Listener recruit = program.recruit(user);
        assertRecruitConsistency(user, program, recruit);
    }

    private void assertRecruitConsistency(User user, Program program, Listener recruit) {
        assertNotNull(recruit);
        assertTrue(user.asListener().isPresent());
        assertSame(recruit, user.asListener().get());
        assertTrue(recruit.getLogin().isPresent());
        assertSame(user, recruit.getLogin().get());

        assertTrue(program.getListeners().contains(recruit));

        Listener recruit2 = program.recruit(user);
        assertSame(recruit, recruit2);

        assertTrue(recruit.getClub().isPresent());
        assertSame(program, recruit.getClub().get());
    }

    @Test
    public void recruitExistingUserAsListenerForClub() {
        User user = new User();
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        Club club = program.addClub(TnTCurriculum.get());

        Listener recruit = club.recruit(user);
        assertNotNull(recruit);
        assertTrue(user.asListener().isPresent());
        assertSame(recruit, user.asListener().get());
        assertTrue(recruit.getLogin().isPresent());
        assertSame(user, recruit.getLogin().get());

        assertTrue(club.getListeners().contains(recruit));
        assertTrue(program.getListeners().contains(recruit));

        Listener recruit2 = program.recruit(user);
        assertSame(recruit, recruit2);
        Listener recruit3 = club.recruit(user);
        assertSame(recruit, recruit3);

        assertTrue(recruit.getClub().isPresent());
        assertSame(club, recruit.getClub().get());
    }

    @Test
    public void aParentCanBeAListenerToo() {
        RegistrationTest registrationTest = new RegistrationTest();
        registrationTest.setup();
        User user = registrationTest.wholeFamilyRegistration();
        Program program = registrationTest.getProgram();

        Listener recruit = program.recruit(user);

        assertRecruitConsistency(user, program, recruit);
    }

    @Test
    public void lookupClub() {
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        Club club = program.addClub(TnTCurriculum.get());

        assertFound(club, program.lookupClub("TnT"));
        assertFalse(program.lookupClub("SomethingElse").isPresent());
    }

    private void assertFound(Club club, Optional<Club> result) {
        assertTrue(result.isPresent());
        assertEquals(club, result.get());
    }
}