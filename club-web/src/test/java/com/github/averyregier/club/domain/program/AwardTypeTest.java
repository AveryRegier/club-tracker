package com.github.averyregier.club.domain.program;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AwardTypeTest {
    @Test
    public void book() {
        assertTrue(AwardType.book.isBook());
    }

    @Test
    public void group() {
        assertFalse(AwardType.group.isBook());
    }
}