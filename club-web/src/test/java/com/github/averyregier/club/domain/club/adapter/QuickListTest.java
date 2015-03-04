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
        listener.getUpdater().setGender(Person.Gender.MALE);
        Clubber clubber = DomainTestUtil.registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertNotNull(quickList);
        assertEquals(1, quickList.size());
        assertEquals(new HashSet<>(Arrays.asList(clubber)), quickList);
    }

    @Test
    public void sameGenderPolicyQuickList() {
        person.getUpdater().setGender(Person.Gender.MALE);

        // in the future add a policy, for now, just make it default

        Listener listener = club.recruit(person);
        DomainTestUtil.registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.FEMALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertEmpty(quickList);

    }

    @Test
    public void noGenderNoAutoQuickList() {
        // in the future add a policy, for now, just make it default

        Listener listener = club.recruit(person);
        DomainTestUtil.registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.FEMALE);
        DomainTestUtil.registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, null);
        DomainTestUtil.registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertEmpty(quickList);

    }

    @Test
    public void preferClubbersWorkedWithBefore() {
        listener.getUpdater().setGender(Person.Gender.MALE);
        HashSet<Clubber> expected = new HashSet<>();
        Clubber clubber = DomainTestUtil.registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        expected.add(clubber);
        clubber.getRecord(clubber.getNextSection()).ifPresent(r->r.sign(listener, ""));

        Set<Clubber> quickList = listener.getQuickList();
        assertNotNull(quickList);
        assertEquals(expected.size(), quickList.size());
        assertEquals(expected, quickList);
    }

}