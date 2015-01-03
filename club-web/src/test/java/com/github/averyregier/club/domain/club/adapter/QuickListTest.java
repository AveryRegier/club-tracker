package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.program.AgeGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.averyregier.club.TestUtility.assertEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuickListTest {
    private Person person;
    private ProgramAdapter program;
    private Club club;
    private Listener listener;


    @Before
    public void setup() {
        person = new PersonAdapter();
        person.getUpdater().setGender(Person.Gender.MALE);
        program = new ProgramAdapter("en_US", null, "AWANA");
        club = program.addClub(program.getCurriculum().getSeries("TnT").get());
        listener = club.recruit(person);
    }

    @Test
    public void noClubbersQuickList() {
        Set<Clubber> quickList = listener.getQuickList();
        assertEmpty(quickList);
    }

    @Test
    public void oneClubberQuickList() {
        Listener listener = club.recruit(person);
        listener.getUpdater().setGender(Person.Gender.MALE);
        Clubber clubber = registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertNotNull(quickList);
        assertEquals(1, quickList.size());
        assertEquals(new HashSet<>(Arrays.asList(clubber)), quickList);
    }

    @Test
    public void sameGenderPolicyQuickList() {
        Person person = new PersonAdapter();
        person.getUpdater().setGender(Person.Gender.MALE);
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        Club club = program.addClub(program.getCurriculum().getSeries("TnT").get());

        // in the future add a policy, for now, just make it default

        Listener listener = club.recruit(person);
        registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.FEMALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertEmpty(quickList);

    }

    @Test
    public void noGenderNoAutoQuickList() {
        Person person = new PersonAdapter();
        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        Club club = program.addClub(program.getCurriculum().getSeries("TnT").get());

        // in the future add a policy, for now, just make it default

        Listener listener = club.recruit(person);
        registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.FEMALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertEmpty(quickList);

    }

    private Clubber registerClubber(ProgramAdapter program, AgeGroup.DefaultAgeGroup grade, Person.Gender gender) {
        ClubberAdapter clubber = new ClubberAdapter(new PersonAdapter());
        clubber.getUpdater().setAgeGroup(grade);
        clubber.getUpdater().setGender(gender);
        program.register(clubber);
        return clubber;
    }

}