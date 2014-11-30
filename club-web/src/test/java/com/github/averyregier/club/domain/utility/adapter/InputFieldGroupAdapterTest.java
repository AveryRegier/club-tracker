package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.utility.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        assertEquals("foo.bar", field.getId());
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
        assertEquals("foo.baz", group.getId());
        assertEquals("foo.baz.bar", group.getFields().get(0).getId());
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
        assertEquals("foo.name", name.getId());
        assertEquals(classUnderTest, name.getContainer());
        assertFalse(name.asGroup().isPresent());
        assertTrue(name.asField().isPresent());
        assertEquals(name, name.asField().get());

        InputFieldDesignator address = designators.get(1);
        assertEquals("foo.address", address.getId());
        assertEquals(classUnderTest, address.getContainer());
        assertTrue(address.asGroup().isPresent());
        assertFalse(address.asField().isPresent());
        assertEquals(address, address.asGroup().get());

        InputFieldDesignator allergies = designators.get(2);
        assertEquals("foo.allergies", allergies.getId());
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

    @Test
    public void validate() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("name")
                .field(f -> f.id("first").type(InputField.Type.text))
                .field(f -> f.id("last").type(InputField.Type.text))
                .validate((p, m) -> Optional.of(m.get("first").toString() + m.get("last").toString()))
                .build();
        HashMap<String, String> map = new HashMap<>();
        map.put("first", "First");
        map.put("last", "Last");

        assertEquals("FirstLast", classUnderTest.validate(map).get());
    }

    @Test
    public void validationFailure() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("name")
                .field(f -> f.id("first").type(InputField.Type.text).required())
                .field(f -> f.id("last").type(InputField.Type.integer).required())
                .validate((p, m) -> Optional.of(m.get("first").toString() + m.get("last").toString()))
                .build();
        HashMap<String, String> map = new HashMap<>();
        map.put("first", "First");
        map.put("last", "Last");

        assertFalse(classUnderTest.validate(map).isPresent());
    }

    @Test
    public void defaultValidation() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("name")
                .field(f -> f.id("first").type(InputField.Type.text))
                .field(f -> f.id("age").type(InputField.Type.integer))
                .build();
        HashMap<String, String> map = new HashMap<>();
        map.put("first", "First");
        map.put("age", "13");

        Optional<Object> result = classUnderTest.validate(map);
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof Map);
        assertEquals("First", ((Map) result.get()).get("first"));
        assertEquals(13, ((Map)result.get()).get("age"));
    }


    @Test
    public void validationMultiLevelFailure() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("upper")
                .group(g->g.id("name")
                    .field(f -> f.id("first").type(InputField.Type.text).required())
                    .field(f -> f.id("last").type(InputField.Type.integer).required())
                    .validate((p, m) -> Optional.of(m.get("first").toString() + m.get("last").toString())))
                .validate((p,m) -> Optional.of(m.get("name")))
                .build();
        HashMap<String, String> map = new HashMap<>();
        map.put("name.first", "First");
        map.put("name.last", "Last");

        assertFalse(classUnderTest.validate(map).isPresent());
    }

    @Test
    public void validationMultiLevel() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("upper")
                .group(g -> g.id("name")
                        .field(f -> f.id("first").type(InputField.Type.text).required())
                        .field(f -> f.id("last").type(InputField.Type.text).required())
                        .validate((p, m) -> Optional.of(m.get("first").toString() + m.get("last").toString())))
                .validate((p, m) -> Optional.of(m.get("name")).map(s -> s.toString().toUpperCase()))
                .build();
        HashMap<String, String> map = new HashMap<>();
        map.put("name.first", "First");
        map.put("name.last", "Last");

        assertEquals("FIRSTLAST", classUnderTest.validate(map).get());
    }

    @Test
    public void map() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("name")
                .field(f -> f.id("first").type(InputField.Type.text))
                .field(f -> f.id("last").type(InputField.Type.text))
                .map(p -> {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("first", p.getName().getGivenName());
                    map.put("last", p.getName().getSurname());
                    return map;
                })
                .build();

        HashMap<String, String> expected = new HashMap<>();
        expected.put("first", "First");
        expected.put("last", "Last");

        User user = new User();
        user.setName("First", "Last");

        assertEquals(expected, classUnderTest.map(user));

    }

    @Test
    public void defaultGroupMap() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .id("name")
                .field(f -> f.id("first")
                        .type(InputField.Type.text)
                        .map(p->p.getName().getGivenName()))
                .field(f -> f.id("last")
                        .type(InputField.Type.text)
                        .map(p->p.getName().getSurname()))
                .build();

        HashMap<String, String> expected = new HashMap<>();
        expected.put("first", "First");
        expected.put("last", "Last");

        User user = new User();
        user.setName("First", "Last");

        assertEquals(expected, classUnderTest.map(user));
    }

    @Test
    public void defaultMultiLevelGroupMap() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .group(g->g
                    .id("name")
                    .field(f -> f.id("first")
                            .type(InputField.Type.text)
                            .map(p->p.getName().getGivenName()))
                    .field(f -> f.id("last")
                            .type(InputField.Type.text)
                            .map(p->p.getName().getSurname())))
                .build();

        HashMap<String, String> expected = new HashMap<>();
        expected.put("name.first", "First");
        expected.put("name.last", "Last");

        User user = new User();
        user.setName("First", "Last");

        assertEquals(expected, classUnderTest.map(user));
    }

    @Test
    public void optionalIsOptional() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .group(g->g
                        .id("name")
                        .field(f -> f.id("first")
                                .type(InputField.Type.text)
                                .required())
                        .field(f -> f.id("last")
                                .type(InputField.Type.text)))
                .build();

        Map<String, String> map = UtilityMethods.map("name.first", "Green").build();
        Optional<Object> result = classUnderTest.validate(map);
        assertTrue(result.isPresent());
        Map resultMap = (Map) result.get();
        assertEquals(1, resultMap.size());
        Map nameMap = (Map) resultMap.get("name");
        assertEquals("Green", nameMap.get("first"));
    }

    @Test
    public void requiredIsRequired() {
        InputFieldGroup classUnderTest = new InputFieldGroupBuilder()
                .group(g -> g
                        .id("name")
                        .field(f -> f.id("first")
                                .type(InputField.Type.text))
                        .field(f -> f.id("last")
                                .type(InputField.Type.text)
                                .required()))
                .build();

        Map<String, String> map = UtilityMethods.map("name.first", "Green").build();
        Optional<Object> result = classUnderTest.validate(map);
        assertFalse(result.isPresent());
    }

}