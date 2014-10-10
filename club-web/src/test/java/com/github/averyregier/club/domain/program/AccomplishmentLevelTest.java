package com.github.averyregier.club.domain.program;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccomplishmentLevelTest {
    @Test
    public void book() {
        assertTrue(AccomplishmentLevel.book.isBook());
    }

    @Test
    public void group() {
        assertFalse(AccomplishmentLevel.group.isBook());
    }
}