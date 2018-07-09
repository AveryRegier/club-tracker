package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.github.averyregier.club.TestUtility.assertEmpty;
import static com.github.averyregier.club.domain.club.adapter.DomainTestUtil.registerClubber;
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
        Clubber clubber = registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertNotNull(quickList);
        assertEquals(1, quickList.size());
        assertEquals(new HashSet<>(Arrays.asList(clubber)), quickList);
    }

    @Test
    public void sameGenderPolicyQuickList() {
        person.getUpdater().setGender(Person.Gender.MALE);

        club.replacePolicies(EnumSet.of(Policy.listenerGroupsByGender), new SettingsAdapter(club));

        Listener listener = club.recruit(person);
        registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.FEMALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertEmpty(quickList);

    }

    @Test
    public void noGenderNoAutoQuickList() {
        club.replacePolicies(EnumSet.of(Policy.listenerGroupsByGender), new SettingsAdapter(club));

        Listener listener = club.recruit(person);
        registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.FEMALE);
        registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, null);
        registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        Set<Clubber> quickList = listener.getQuickList();
        assertEmpty(quickList);

    }

    @Test
    public void preferClubbersWorkedWithBefore() {
        listener.getUpdater().setGender(Person.Gender.MALE);
        HashSet<Clubber> expected = new HashSet<>();
        Clubber clubber = registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        expected.add(clubber);
        clubber.getRecord(clubber.getNextSection()).ifPresent(r -> r.sign(listener, ""));

        Set<Clubber> quickList = listener.getQuickList();
        assertNotNull(quickList);
        assertEquals(expected.size(), quickList.size());
        assertEquals(expected, quickList);
    }


    @Test
    public void alhabetizedList() {
        listener.getUpdater().setGender(Person.Gender.MALE);
        TreeSet<Clubber> expected = new TreeSet<>();
        addClubberWithName(expected, "A", "Z");
        addClubberWithName(expected, "B", "Y");
        addClubberWithName(expected, "A", "Y");
        addClubberWithName(expected, "C", "X");

        Set<Clubber> quickList = listener.getQuickList();
        assertNotNull(quickList);
        assertEquals(expected.size(), quickList.size());
        assertSameOrder(expected, quickList);
    }

    private <T> void assertSameOrder(Collection<? extends T> expected, Collection<? extends T> checking) {
        Iterator<? extends T> iterator = checking.iterator();
        for (T e : expected) {
            assertEquals(e, iterator.next());
        }
    }

    private void addClubberWithName(TreeSet<Clubber> expected, String given, String surname) {
        Clubber clubber = registerClubber(program, AgeGroup.DefaultAgeGroup.THIRD_GRADE, Person.Gender.MALE);
        clubber.getUpdater().setName(new NameBuilder().given(given).surname(surname).build());
        expected.add(clubber);
    }

}