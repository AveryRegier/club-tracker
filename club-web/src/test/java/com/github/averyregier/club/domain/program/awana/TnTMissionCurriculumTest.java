package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TnTMissionCurriculumTest {
    Curriculum classUnderTest = TnTMissionCurriculum.get();

    @Test
    public void seventhGradeGetsNoBooks() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.SEVENTH_GRADE);
        assertNotNull(recommendation);
        assertEquals(0, recommendation.size());
    }

    @Test
    public void secondGradeGetsNoBooks() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.SECOND_GRADE);
        assertNotNull(recommendation);
        assertEquals(0, recommendation.size());
    }

    @Test
    public void allBooksHaveProperID() {
        for(Book book: classUnderTest.getBooks()) {
            assertEquals(
                    book.getContainer().getId()+":"+book.getShortCode()+book.getVersion(),
                    book.getId());
        }
    }

    @Test
    public void curriculumID() {
        assertEquals("TnT", classUnderTest.getId());
        assertEquals("TnT", classUnderTest.getShortCode());
    }

}