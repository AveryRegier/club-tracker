package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.InputField;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class InputFieldAdapterTest {

    @Test
    public void hasType() {
        InputField classUnderTest = new InputFieldBuilder()
                .type(InputField.Type.text)
                .build();
        assertEquals(InputField.Type.text, classUnderTest.getType());
    }

    @Test
    public void hasName() {
        InputField classUnderTest = new InputFieldBuilder()
                .name("a name")
                .build();
        assertEquals("a name", classUnderTest.getName());
    }

    @Test
    public void hasId() {
        InputField classUnderTest = new InputFieldBuilder()
                .id("id1")
                .build();
        assertEquals("id1", classUnderTest.getShortCode());
        assertEquals("id1", classUnderTest.getId());
    }

    @Test
    public void noValues() {
        InputField classUnderTest = new InputFieldBuilder().build();
        assertFalse(classUnderTest.getValues().isPresent());
    }

    @Test
    public void oneValue() {
        InputField classUnderTest = new InputFieldBuilder()
                .value("value1")
                .build();
        assertTrue(classUnderTest.getValues().isPresent());
        assertEquals("value1", classUnderTest.getValues().get().get(0).getValue());
    }

    @Test
    public void oneNamedValue() {
        InputField classUnderTest = new InputFieldBuilder()
                .value("value1", "Value One", false)
                .build();
        assertTrue(classUnderTest.getValues().isPresent());
        assertEquals("value1", classUnderTest.getValues().get().get(0).getValue());
        assertEquals("Value One", classUnderTest.getValues().get().get(0).getDisplayName());
        assertEquals(false, classUnderTest.getValues().get().get(0).isDefault());
    }

    @Test
    public void threeValues() {
        InputField classUnderTest = new InputFieldBuilder()
                .value("value1", "Value One", false)
                .value("value2", "Value Two", true)
                .value("value3")
                .build();
        assertTrue(classUnderTest.getValues().isPresent());
        assertEquals("value1", classUnderTest.getValues().get().get(0).getValue());
        InputField.Value value = classUnderTest.getValues().get().get(1);
        assertEquals("value2", value.getValue());
        assertEquals(true, value.isDefault());
        InputField.Value three = classUnderTest.getValues().get().get(2);
        assertEquals("value3", three.getValue());
        assertEquals(false, three.isDefault());
        assertEquals("value3", three.getDisplayName());
    }

    @Test
    public void validateFreeFormString() {
        InputField classUnderTest = new InputFieldBuilder()
                .type(InputFieldAdapter.Type.text)
                .build();
        Optional<Object> validate = classUnderTest.validate("foo bar");
        assertTrue(validate.isPresent());
        assertEquals("foo bar", validate.get());
    }

    @Test
    public void nullsDontValidate() {
        InputField classUnderTest = new InputFieldBuilder()
                .type(InputFieldAdapter.Type.text)
                .build();
        assertFalse(classUnderTest.validate(null).isPresent());
    }

    @Test
    public void validateIntegerAgainstIntegerField() {
        InputField classUnderTest = new InputFieldBuilder()
                .type(InputFieldAdapter.Type.integer)
                .build();
        Optional<Object> validate = classUnderTest.validate("1234");
        assertTrue(validate.isPresent());
        assertEquals(1234, validate.get());
    }

    @Test
    public void validateDoubleAgainstIntegerFieldFails() {
        InputField classUnderTest = new InputFieldBuilder()
                .type(InputFieldAdapter.Type.integer)
                .build();
        Optional<Object> validate = classUnderTest.validate("12.34");
        assertFalse(validate.isPresent());
    }
}