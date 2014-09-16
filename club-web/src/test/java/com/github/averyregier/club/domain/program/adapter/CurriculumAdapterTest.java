package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CurriculumAdapterTest {
    @Test
    public void shortCode() {
        Curriculum curriculum = new CurriculumBuilder().shortCode("AWANA").build();
        assertEquals("AWANA", curriculum.getShortCode());
        assertEquals("AWANA", curriculum.getId());
    }

    @Test
    public void book() {
        Curriculum curriculum = new CurriculumBuilder()
                .shortCode("TnT")
                .book(0, b -> b.shortCode("SZ1").version(1, 0))
                .build();

        Book book = curriculum.getBooks().get(0);
        assertEquals(book, curriculum.getBooks().get(0));
        assertEquals(curriculum, book.getContainer());
        assertEquals("TnT:SZ1v1.0", book.getId());
    }

    @Test
    public void ids() {
        Curriculum curriculum = new CurriculumBuilder()
                .shortCode("C")
                .book(2, b -> b
                        .shortCode("B1")
                        .version(1, 3)
                        .group(3, g -> g
                                .section(5, TnTSectionTypes.parent)))
                .build();

        Book book = curriculum.getBooks().get(0);
        assertEquals("C:B1v1.3", book.getId());
        SectionGroup group = book.getSectionGroups().get(0);
        assertEquals("C:B1v1.3:3", group.getId());
        assertEquals("C:B1v1.3:3:5", group.getSections().get(0).getId());
    }

    @Test
    public void lookup() {
        Curriculum curriculum = new CurriculumBuilder()
                .shortCode("C")
                .book(2, b -> b
                        .shortCode("B1")
                        .version(1, 3)
                        .group(3, g -> g
                                .section(3, TnTSectionTypes.parent)
                                .section(5, TnTSectionTypes.parent)
                                .section(6, TnTSectionTypes.parent)))
                .build();

        assertFalse(curriculum.lookup("C:B1v1.3:3:4").isPresent());
        assertFalse(curriculum.lookup("C:B1v1.3:2:5").isPresent());
        assertFalse(curriculum.lookup("C:B1v1.2:3:5").isPresent());
        assertFalse(curriculum.lookup("C:B1v2.3:3:5").isPresent());
        assertFalse(curriculum.lookup("C:B2v1.3:3:5").isPresent());
        assertFalse(curriculum.lookup("C:C1v1.3:3:5").isPresent());
        assertFalse(curriculum.lookup("D:B1v1.3:3:5").isPresent());

        assertEquals(curriculum.getBooks().get(0).getSectionGroups().get(0).getSections().get(1),
                curriculum.lookup("C:B1v1.3:3:5").get());
        assertEquals(curriculum.getBooks().get(0).getSectionGroups().get(0).getSections().get(2),
                curriculum.lookup("C:B1v1.3:3:6").get());
    }

}