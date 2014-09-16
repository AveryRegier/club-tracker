package com.github.averyregier.club.domain.program.adapter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookVersionAdapterTest {
    @Test
    public void tostring() {
        assertEquals("v1.3",
                new BookVersionAdapter(1,3,null,null,null).toString());
    }

}