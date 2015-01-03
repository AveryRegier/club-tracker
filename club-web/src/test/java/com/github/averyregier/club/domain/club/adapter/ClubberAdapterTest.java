package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        Optional<Section> section = clubber.getNextSection();
        assertNotNull(section);
        assertTrue(section.isPresent());
        Section firstSection = getFirstSection(clubber.getCurrentAgeGroup());
        assertEquals(firstSection, section.get());
    }

    @Test
    public void testGetSecondSection() {
        Section firstSection = getFirstSection(clubber.getCurrentAgeGroup());
        clubber.getRecord(Optional.of(firstSection)).ifPresent(r -> r.sign(mockListener, ""));
        Optional<Section> section = clubber.getNextSection();
        assertNotNull(section);
        assertTrue(section.isPresent());
        Section secondSection = firstSection.getGroup().getSections().get(1);
        assertEquals(secondSection, section.get());
    }

    private Section getFirstSection(AgeGroup ageGroup) {
        Optional<Book> firstBook = club.getCurriculum()
                .recommendedBookList(ageGroup).stream().findFirst();
        Optional<Section> firstSection = firstBook.get().getSections().stream().findFirst();
        return firstSection.get();
    }
}