package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.program.adapter.MasterCurriculum;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.domain.utility.adapter.InputFieldBuilder;
import org.junit.Test;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.*;

public class ProgramAdapterTest {
    @Test
    public void noClubsToStartWith() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, null, (String)null);
        assertTrue(classUnderTest.getClubs().isEmpty());
    }

    @Test
    public void orgName() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, "An org name", (String)null);
        assertEquals("An org name", classUnderTest.getShortCode());
    }

    @Test
    public void locale() {
        ProgramAdapter classUnderTest = new ProgramAdapter("en_GB", null, (String)null);
        assertEquals(new Locale("en", "GB"), classUnderTest.getLocale());
    }

    @Test
    public void curriculum() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, null, "AWANA");
        assertEquals(Programs.AWANA.get(), classUnderTest.getCurriculum());
    }

    @Test
    public void addClub() {
        ProgramAdapter classUnderTest = new ProgramAdapter(null, null, (String)null);
        MasterCurriculum curriculum = new MasterCurriculum("foo", null, Collections.emptyList(), null);
        classUnderTest.addClub(curriculum);
        assertFalse(classUnderTest.getClubs().isEmpty());
        assertEquals("foo", classUnderTest.getClubs().iterator().next().getShortCode());
    }

    @Test
    public void registerClubber() {
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        program.addClub(TnTCurriculum.get());

        ClubberAdapter clubber = new ClubberAdapter();
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.THIRD_GRADE);
        program.register(clubber);

        assertTrue(program.getClubbers().contains(clubber));
        assertTrue(clubber.getClub().isPresent());
        Club clubbersClub = clubber.getClub().get();
        assertTrue(program.getClubs().contains(clubbersClub));
        assertEquals("TnT", clubbersClub.getShortCode());
    }

    @Test
    public void registerClubberFails() {
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        program.addClub(TnTCurriculum.get());

        ClubberAdapter clubber = new ClubberAdapter();
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.COLLEGE);
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
        ProgramAdapter program = new ProgramAdapter("en_US", "Foo", "AWANA");
        Club club = program.addClub(TnTCurriculum.get());

        assertFound(club, program.lookupClub("TnT"));
        assertFalse(program.lookupClub("SomethingElse").isPresent());
        assertFound(program, program.lookupClub("Foo"));
    }

    private void assertFound(Club club, Optional<Club> result) {
        assertTrue(result.isPresent());
        assertEquals(club, result.get());
    }

    @Test
    public void assignLeaderToProgram() {
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        Club club = program.addClub(TnTCurriculum.get()); // should ignore the club even though it is present
        User user = new User();
        ClubLeader.LeadershipRole leadershipRole = ClubLeader.LeadershipRole.random();
        ClubLeader leader = program.assign(user, leadershipRole);

        assertNotNull(leader);
        assertTrue(user.asClubLeader().isPresent());
        assertSame(leader, user.asClubLeader().get());
        assertEquals(program, leader.getProgram());
        assertTrue(leader.getLogin().isPresent());
        assertEquals(user, leader.getLogin().get());

        assertTrue(leader.getClub().isPresent());
        assertEquals(program, leader.getClub().get());

        assertEquals(leadershipRole, leader.getLeadershipRole());
    }

    @Test
    public void assignLeaderToClub() {
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        Club club = program.addClub(TnTCurriculum.get());
        User user = new User();
        ClubLeader.LeadershipRole leadershipRole = ClubLeader.LeadershipRole.random();
        ClubLeader leader = club.assign(user, leadershipRole);

        assertNotNull(leader);
        assertTrue(user.asClubLeader().isPresent());
        assertSame(leader, user.asClubLeader().get());
        assertEquals(program, leader.getProgram());
        assertTrue(leader.getLogin().isPresent());
        assertEquals(user, leader.getLogin().get());

        assertTrue(leader.getClub().isPresent());
        assertEquals(club, leader.getClub().get());

        assertEquals(leadershipRole, leader.getLeadershipRole());
    }

    @Test
    public void addRegistrationFieldsToProgram() {
        Program program = new ProgramAdapter("en_US", null, "AWANA");

        InputField parentField = new InputFieldBuilder()
                .name("pField")
                .id("a")
                .required()
                .type(InputField.Type.text)
                .build();

        assertEquals(program, program.addField(RegistrationSection.parent, parentField));

        InputField householdField = new InputFieldBuilder()
                .name("hField")
                .id("b")
                .type(InputField.Type.text)
                .build();

        assertEquals(program, program.addField(RegistrationSection.household, householdField));

        InputField childField = new InputFieldBuilder()
                .name("cField")
                .id("c")
                .required()
                .value("N/A")
                .type(InputField.Type.text)
                .build();

        assertEquals(program, program.addField(RegistrationSection.child, childField));

        RegistrationInformation form = program.createRegistrationForm();

        assertFieldInGroup(form, 0, parentField);
        assertFieldInGroup(form, 1, householdField);

        form = program.updateRegistrationForm(UtilityMethods.map("action", "child").build());

        assertFieldInGroup(form, 0, parentField);
        assertFieldInGroup(form, 1, householdField);
        assertFieldInGroup(form, 2, childField);
    }

    @Test
    public void defaultTimeZoneIsCT() {
        Program program = new ProgramAdapter("en_US", null, "AWANA");
        ZoneId tz = program.getTimeZone();
        assertEquals(ZoneId.of("CST6CDT"), tz);
    }

    private void assertFieldInGroup(RegistrationInformation form, int index, InputField field) {
        assertTrue(form.getForm()
                .get(index).asGroup().get()
                .getFields().stream()
                .anyMatch(f -> compare(field, f)));
    }

    private boolean compare(InputField a, InputField b) {
        return a.getName().equals(b.getName());
    }
}