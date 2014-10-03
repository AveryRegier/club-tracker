package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.InputField;
import com.github.averyregier.club.domain.club.InputFieldGroup;
import org.junit.Test;

import static org.junit.Assert.*;

public class InputFieldAdapterGroupTest {

    @Test
    public void id() throws Exception {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("an id")
                .name("descriptor")
                .build();

        assertEquals("an id", classUnderTest.getId());
        assertEquals("an id", classUnderTest.getShortCode());
        assertEquals("descriptor", classUnderTest.getName());

    }

    @Test
    public void name() throws Exception {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .name("descriptor")
                .build();

        assertEquals("descriptor", classUnderTest.getName());
    }

    @Test
    public void noFields() throws Exception {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .build();

        assertTrue(classUnderTest.getFields().isEmpty());
    }

    @Test
    public void parent() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .field(f -> f)
                .build();

        InputField field = classUnderTest.getFields().get(0);
        assertNotNull(field);
        assertEquals(classUnderTest, field.getContainer());
    }

    @Test
    public void parentID() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("foo")
                .field(f -> f.id("bar"))
                .build();

        InputField field = classUnderTest.getFields().get(0);
        assertEquals("bar", field.getShortCode());
        assertEquals("foo:bar", field.getId());
        assertNull(classUnderTest.getContainer());
    }

    @Test
    public void grandparent() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("foo")
                .group(g -> g
                                .id("baz")
                                .field(f -> f.id("bar"))
                )
                .build();

        assertTrue(classUnderTest.getFields().isEmpty());
        assertFalse(classUnderTest.getGroups().isEmpty());
        InputFieldGroup group = classUnderTest.getGroups().get(0);
        assertEquals("baz", group.getShortCode());
        assertEquals(classUnderTest, group.getContainer());
        assertEquals("foo:baz", group.getId());
        assertEquals("foo:baz:bar", group.getFields().get(0).getId());
        assertNull(classUnderTest.getContainer());
    }
}