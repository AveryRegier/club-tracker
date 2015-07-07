package com.github.averyregier.club.domain.utility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by avery on 7/6/15.
 */
public class TrackedStringTest extends TrackedFieldTest {

    @Test
    public void ifEmptySet() {
        TrackedString field = new TrackedString("");
        field.ifEmpty(() -> "updated");
        assertEquals("updated", field.getValue());
        assertTrue(field.isChanged());
    }

    @Test
    public void ifDefaultNotEmptyDontSet() {
        TrackedString field = new TrackedString("default");
        field.ifEmpty(()->"updated");
        assertEquals("default", field.getValue());
        assertFalse(field.isChanged());
    }

    @Override
    protected TrackedField<String> create(String aDefault) {
        return new TrackedString(aDefault);
    }
}