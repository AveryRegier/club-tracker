package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TnTCurriculumTest {
    Curriculum classUnderTest = TnTCurriculum.get();

    @Test
    public void thirdGradeBookRecommendation() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.THIRD_GRADE);
        assertNotNull(recommendation);
        assertTrue(recommendation.size()>=1);
        assertSame(0, recommendation.get(0).sequence());
    }

    @Test
    public void allBooksHaveProperID() {

        for(Book book: classUnderTest.getBooks()) {
            assertEquals(classUnderTest.getId()+":"+book.getShortCode()+book.getVersion(), book.getId());

        }
    }

    @Test
    public void curriculumID() {
        assertEquals("TnT", classUnderTest.getId());
        assertEquals("TnT", classUnderTest.getShortCode());
    }
}
