package com.github.averyregier.club.domain.program.adapter;

import org.junit.Test;

import java.time.Year;

import static org.junit.Assert.assertEquals;

public class BookVersionAdapterTest {
    @Test
    public void tostring() {
        assertEquals("v1.3",
                new BookVersionAdapter(1,3,null,null,null).toString());
    }

    @Test
    public void tostringYearOnly() {
        assertEquals("c2010",
                new BookVersionAdapter(0,0,null,null, Year.of(2010)).toString());
    }
}