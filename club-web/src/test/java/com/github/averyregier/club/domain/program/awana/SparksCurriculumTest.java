package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.*;
import org.junit.Test;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

import static com.github.averyregier.club.domain.program.awana.SparksSectionTypes.friend;
import static com.github.averyregier.club.domain.program.awana.SparksSectionTypes.regular;
import static org.junit.Assert.*;

/**
 * Created by avery on 9/6/15.
 */
public class SparksCurriculumTest {
    Curriculum classUnderTest = SparksCurriculum.get();

    @Test
    public void allBooksHaveProperID() {
        for(Book book: classUnderTest.getBooks()) {
            assertEquals(
                    book.getContainer().getId()+":"+book.getShortCode()+book.getVersion(),
                    book.getId());
        }
    }

    @Test
    public void secondGradeBookRecommendation() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.SECOND_GRADE);
        assertNotNull(recommendation);
        assertEquals(5, recommendation.size());
        assertSame(0, recommendation.get(0).sequence());
        assertSame(1, recommendation.get(1).sequence());
        assertSame(3, recommendation.get(2).sequence());
        assertSame(4, recommendation.get(3).sequence());
        assertSame(5, recommendation.get(4).sequence());
    }


    @Test
    public void firstGradeBookRecommendation() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.FIRST_GRADE);
        assertNotNull(recommendation);
        assertEquals(4, recommendation.size());
        assertSame(0, recommendation.get(0).sequence());
        assertSame(1, recommendation.get(1).sequence());
        assertSame(3, recommendation.get(2).sequence());
        assertSame(4, recommendation.get(3).sequence());
    }

    @Test
    public void kindergartenBookRecommendation() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.KINDERGARTEN);
        assertNotNull(recommendation);
        assertEquals(3, recommendation.size());
        assertSame(0, recommendation.get(0).sequence());
        assertSame(1, recommendation.get(1).sequence());
        assertSame(2, recommendation.get(2).sequence());
    }

    @Test
    public void curriculumID() {
        assertEquals("Sparks", classUnderTest.getId());
        assertEquals("Sparks", classUnderTest.getShortCode());
    }

    @Test
    public void specialSections() {
        assertEquals(friend, classUnderTest.lookup(classUnderTest.getId()+":1c2010:RJ1:1").get().getSectionType());
        assertEquals(friend, classUnderTest.lookup(classUnderTest.getId()+":2c2010:RJ1:1").get().getSectionType());
        assertEquals(friend, classUnderTest.lookup(classUnderTest.getId()+":3c2010:RJ1:1").get().getSectionType());
    }

    @Test
    public void lookupViaAwana() {
        assertEquals(friend, Programs.AWANA
                .get().getSeries("AWANA:Sparks")
                .get().lookup("AWANA:Sparks:1c2010:RJ1:1")
                .get().getSectionType());
    }

    @Test
    public void flight316EntranceTest() {
        Book j316 = classUnderTest.getBooks().get(0);
        assertEquals("F316", j316.getShortCode());
        assertEquals("F316", j316.getMwhCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.KINDERGARTEN,
                AgeGroup.DefaultAgeGroup.FIRST_GRADE,
                AgeGroup.DefaultAgeGroup.SECOND_GRADE), j316.getAgeGroups());
        assertEquals(Year.of(2010), j316.getVersion().getPublicationYear());
        assertEquals("c2010", j316.getVersion().toString());
        assertEquals(0, j316.sequence());
        assertEquals("Flight 3:16", j316.getName());
        assertEquals("Sparks Vest",
                j316.getSections().get(0).getAwards(AccomplishmentLevel.group).iterator().next().getName());

        assertJohn316Sections(j316);
    }

    private void assertJohn316Sections(Book sz) {
        assertEquals(6, sz.getSections().size());
        int number = 0;
        for(Section s: sz.getSections()) {
            assertEquals(Integer.toString(++number), s.getShortCode());
            assertEquals(1, s.getAwards(AccomplishmentLevel.group).size());
            assertEquals(regular, s.getSectionType());
            assertEquals(number, s.sequence());
        }
    }

    @Test
    public void hangGlider() {
        Book book = classUnderTest.getBooks().get(1);
        assertEquals("1", book.getShortCode());
        assertEquals("HG", book.getMwhCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.KINDERGARTEN,
                AgeGroup.DefaultAgeGroup.FIRST_GRADE,
                AgeGroup.DefaultAgeGroup.SECOND_GRADE), book.getAgeGroups());
        assertEquals(Year.of(2010), book.getVersion().getPublicationYear());
        assertEquals("c2010", book.getVersion().toString());
        assertEquals(1, book.sequence());
        assertEquals("Hang Glider", book.getName());
        Award bookAward = book.getSections().get(0).getAwards(AccomplishmentLevel.book)
                .iterator().next();
        assertEquals("First Book Ribbon", bookAward.getName());
        List<Catalogued> bookAwardList = bookAward.list();
        assertEquals(1, bookAwardList.size());
        assertEquals("First Book Ribbon", bookAwardList.get(0).getName());

        assertNormalSparksStructure(book);
    }

    @Test
    public void wingRunner() {
        Book book = classUnderTest.getBooks().get(3);
        assertEquals("2", book.getShortCode());
        assertEquals("WR", book.getMwhCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.FIRST_GRADE,
                AgeGroup.DefaultAgeGroup.SECOND_GRADE), book.getAgeGroups());
        assertEquals(Year.of(2010), book.getVersion().getPublicationYear());
        assertEquals("c2010", book.getVersion().toString());
        assertEquals(3, book.sequence());
        assertEquals("Wing Runner", book.getName());
        Award bookAward = book.getSections().get(0).getAwards(AccomplishmentLevel.book)
                .iterator().next();
        assertEquals("Second Book Ribbon", bookAward.getName());
        List<Catalogued> bookAwardList = bookAward.list();
        assertEquals(1, bookAwardList.size());
        assertEquals("Second Book Ribbon", bookAwardList.get(0).getName());

        assertNormalSparksStructure(book);
    }

    @Test
    public void skyStormer() {
        Book book = classUnderTest.getBooks().get(5);
        assertEquals("3", book.getShortCode());
        assertEquals("SS", book.getMwhCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.SECOND_GRADE), book.getAgeGroups());
        assertEquals(Year.of(2010), book.getVersion().getPublicationYear());
        assertEquals("c2010", book.getVersion().toString());
        assertEquals(4, book.sequence());
        assertEquals("Sky Stormer", book.getName());
        Award bookAward = book.getSections().get(0).getAwards(AccomplishmentLevel.book)
                .iterator().next();
        assertEquals("Sparky Award", bookAward.getName());
        List<Catalogued> bookAwardList = bookAward.list();
        assertEquals(1, bookAwardList.size());
        assertEquals("Sparky Award", bookAwardList.get(0).getName());

        assertNormalSparksStructure(book);
    }

    private void assertNormalSparksStructure(Book book) {
        String[] ids = {
                "RP",
                "RJ1", "GJ1",
                "RJ2", "GJ2",
                "RJ3", "GJ3",
                "RJ4", "GJ4",
                "R"
        };
        String[] names = {
                book.getName()+" Rank Patch",
                "Red Jewel 1", "Green Jewel 1",
                "Red Jewel 2", "Green Jewel 2",
                "Red Jewel 3", "Green Jewel 3",
                "Red Jewel 4", "Green Jewel 4",
                book.getName()+" Review"
        };
        int[] counts = {8,4,4,4,4,4,4,4,4,15};
        assertEquals(counts.length, book.getSectionGroups().size());
        List<SectionGroup> sectionGroups = book.getSectionGroups();
        for (int i = 0; i < sectionGroups.size(); i++) {
            SectionGroup group = sectionGroups.get(i);
            assertEquals(ids[i], group.getShortCode());
            assertEquals(i, group.sequence());

            assertEquals(counts[i], group.getSections().size());
            assertEquals(names[i], group.getName());
        }
    }

}