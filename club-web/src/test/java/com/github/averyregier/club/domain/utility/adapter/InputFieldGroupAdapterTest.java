package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class InputFieldGroupAdapterTest {

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

    @Test
    public void fieldOrder() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("foo")
                .field(f -> f.id("name"))
                .group(g -> g
                                .id("address")
                                .field(f -> f.id("state"))
                )
                .field(f -> f.id("allergies"))
                .build();

        List<InputFieldDesignator> designators = classUnderTest.getFieldDesignations();
        InputFieldDesignator name = designators.get(0);
        assertEquals("foo:name", name.getId());
        assertEquals(classUnderTest, name.getContainer());
        assertFalse(name.asGroup().isPresent());
        assertTrue(name.asField().isPresent());
        assertEquals(name, name.asField().get());

        InputFieldDesignator address = designators.get(1);
        assertEquals("foo:address", address.getId());
        assertEquals(classUnderTest, address.getContainer());
        assertTrue(address.asGroup().isPresent());
        assertFalse(address.asField().isPresent());
        assertEquals(address, address.asGroup().get());

        InputFieldDesignator allergies = designators.get(2);
        assertEquals("foo:allergies", allergies.getId());
        assertEquals(classUnderTest, allergies.getContainer());
        assertFalse(allergies.asGroup().isPresent());
        assertTrue(allergies.asField().isPresent());
        assertEquals(allergies, allergies.asField().get());
    }

    @Test
    public void find() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("foo")
                .field(f -> f.id("name"))
                .group(g -> g
                                .id("address")
                                .field(f -> f.id("state"))
                )
                .group(g -> g
                                .id("hierarchy")
                                .group(g2 -> g2
                                                .id("hierarchy2")
                                                .field(f -> f.id("leaf"))
                                )
                )
                .build();
        assertEquals("name", classUnderTest.find("name").get().getShortCode());
        assertEquals("address", classUnderTest.find("address").get().getShortCode());
        assertEquals("state", classUnderTest.find("address", "state").get().getShortCode());
        assertEquals("leaf", classUnderTest.find("hierarchy", "hierarchy2", "leaf").get().getShortCode());
        assertFalse(classUnderTest.find("hierarchy", "don't exist").isPresent());
        assertFalse(classUnderTest.find("hierarchy", "hierarchy2", "leaf", "too", "many").isPresent());
    }
}