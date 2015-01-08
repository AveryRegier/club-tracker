package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.TestUtility;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.program.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ClubberAdapterTest {
    private ProgramAdapter program;
    private Club club;
    private ClubberAdapter clubber;
    private ListenerAdapter mockListener = new ListenerAdapter(new PersonAdapter());

    @Before
    public void setup() {
        program = new ProgramAdapter("en_US", null, "AWANA");
        club = program.addClub(program.getCurriculum().getSeries("TnT").get());
        clubber = new ClubberAdapter();
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.FIFTH_GRADE);
        program.register(clubber);
    }

    @Test
    public void testGetFirstSection() {
        Optional<Section> section = assertNextSection();
        Section firstSection = getFirstSection(clubber.getCurrentAgeGroup());
        assertEquals(firstSection, section.get());
    }

    @Test
    public void testGetSecondSection() {
        Section firstSection = getFirstSection(clubber.getCurrentAgeGroup());
        clubber.getRecord(Optional.of(firstSection)).ifPresent(r -> r.sign(mockListener, ""));
        Optional<Section> section = assertNextSection();
        Section secondSection = firstSection.getGroup().getSections().get(1);
        assertEquals(secondSection, section.get());
    }

    @Test
    public void testSkipNonRequiredToMoveOn() {
        club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b->b.getSections().stream())
                .filter(s->s.getSectionType().requiredToMoveOn())
                .limit(20)
                .forEach(s->clubber.getRecord(Optional.of(s)).ifPresent(r -> r.sign(mockListener, "")));

        Optional<Section> section = assertNextSection();
        assertTrue(section.get().getSectionType().requiredToMoveOn());
    }

    @Test
    public void testRequiredForRewardBeforeExtraCredit() {
        club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b->b.getSections().stream())
                .filter(s->s.getSectionType().requiredToMoveOn())
                .forEach(s->clubber.getRecord(Optional.of(s)).ifPresent(r -> r.sign(mockListener, "")));

        Optional<Section> section = assertNextSection();
        assertFalse(section.get().getSectionType().requiredToMoveOn());
        assertTrue(section.get().getSectionType().requiredFor(AccomplishmentLevel.book));
    }

    @Test
    public void testExtraCredit() {
        club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b->b.getSections().stream())
                .filter(s->s.getSectionType().requiredFor(AccomplishmentLevel.book))
                .filter(s-> TestUtility.anyEqual(s.getContainer().getBook().sequence(), 0, 1))
                .forEach(s->clubber.getRecord(Optional.of(s)).ifPresent(r -> r.sign(mockListener, "")));

        Optional<Section> section = assertNextSection();
        assertFalse(section.get().getSectionType().requiredFor(AccomplishmentLevel.book));
    }

    @Test
    public void testExtraCreditForCurrentBook() {
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.SIXTH_GRADE);
        club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b -> b.getSections().stream())
                .filter(s->s.getSectionType().requiredFor(AccomplishmentLevel.book))
                .forEach(s->clubber.getRecord(Optional.of(s)).ifPresent(r -> r.sign(mockListener, "")));

        Optional<Section> section = assertNextSection();
        assertFalse(section.get().getSectionType().requiredFor(AccomplishmentLevel.book));
        assertEquals(2, section.get().getContainer().getBook().sequence());
    }

    private Optional<Section> assertNextSection() {
        Optional<Section> section = clubber.getNextSection();
        assertNotNull(section);
        assertTrue(section.isPresent());
        return section;
    }

    @Test
    public void testFinishFirstBookBeforeMovingOn() {
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.SIXTH_GRADE);
        club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b -> b.getSections().stream())
                .filter(s->s.getSectionType().requiredToMoveOn())
                .filter(s -> TestUtility.anyEqual(s.getContainer().getBook().sequence(), 0, 1))
                .forEach(s->clubber.getRecord(Optional.of(s)).ifPresent(r -> r.sign(mockListener, "")));

        Optional<Section> section = assertNextSection();
        assertFalse(section.get().getSectionType().requiredToMoveOn());
        assertTrue(section.get().getSectionType().requiredFor(AccomplishmentLevel.book));
        assertEquals(1, section.get().getContainer().getBook().sequence());
    }

    private Section getFirstSection(AgeGroup ageGroup) {
        Optional<Book> firstBook = club.getCurriculum()
                .recommendedBookList(ageGroup).stream().findFirst();
        Optional<Section> firstSection = firstBook.get().getSections().stream().findFirst();
        return firstSection.get();
    }
}