package com.github.averyregier.club.domain.utility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Test
    public void addSpouse() {
        assertEquals("Add Spouse", Action.spouse.getDisplayName());
    }

}