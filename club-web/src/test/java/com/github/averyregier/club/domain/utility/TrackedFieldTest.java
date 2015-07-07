package com.github.averyregier.club.domain.utility;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by avery on 7/6/15.
 */
public class TrackedFieldTest {

    @Test
    public void defaultValue() {
        TrackedField<String> field = create("default");
        assertEquals("default", field.getValue());
        assertFalse(field.isChanged());
    }

    @Test
    public void updated() {
        TrackedField<String> field = create("default");
        field.setValue("updated");
        assertEquals("updated", field.getValue());
        assertTrue(field.isChanged());
    }

    @Test
    public void same() {
        TrackedField<String> field = create("default");
        field.setValue("default");
        assertEquals("default", field.getValue());
        assertFalse(field.isChanged());
    }

    @Test
    public void force() {
        TrackedField<String> field = create("default");
        field.forceSet("default");
        assertEquals("default", field.getValue());
        assertTrue(field.isChanged());
    }

    @Test
    public void updateNull() {
        TrackedField<String> field = create("default");
        field.setValue(null);
        assertEquals(null, field.getValue());
        assertTrue(field.isChanged());
    }

    @Test
    public void updateEmpty() {
        TrackedField<String> field = create("default");
        field.setValue("");
        assertEquals("", field.getValue());
        assertTrue(field.isChanged());
    }

    @Test
    public void updateEmptyNotChanged() {
        TrackedField<String> field = create("");
        field.setValue("");
        assertEquals("", field.getValue());
        assertFalse(field.isChanged());
    }

    @Test
    public void ifTestEmptySet() {
        TrackedField<String> field = create("");
        field.test(UtilityMethods::isEmpty, () -> "updated");
        assertEquals("updated", field.getValue());
        assertTrue(field.isChanged());
    }

    @Test
    public void ifDefaultTestNotEmptyDontSet() {
        TrackedField<String> field = create("default");
        field.test(UtilityMethods::isEmpty, ()->"updated");
        assertEquals("default", field.getValue());
        assertFalse(field.isChanged());
    }

    protected TrackedField<String> create(String aDefault) {
        return new TrackedField<>(aDefault);
    }
}