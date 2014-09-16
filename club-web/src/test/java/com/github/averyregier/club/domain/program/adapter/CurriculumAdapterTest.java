package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}