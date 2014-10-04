package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.InputField.Type.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StandardInputFieldsTest {

    public static final Locale EN_US = Locale.forLanguageTag("en_US");

    @Test
    public void name() {
        InputFieldGroup group = StandardInputFields.name.createGroup(Locale.forLanguageTag("en_US")).build();
        assertEquals("Name", group.getName());
        assertEquals("name", group.getShortCode());
        assertEquals("name", group.getId());
        List<InputFieldDesignator> designations = group.getFieldDesignations();
        assertFalse(designations.isEmpty());

        assertField(designations.get(0), "Given", "given", text);
        assertField(designations.get(1), "Middle", "middle", text);
        assertField(designations.get(2), "Surname", "surname", text);
        assertField(designations.get(3), "Friendly", "friendly", text);
        assertField(designations.get(4), "Title", "title", text, "", "Dr");
        assertField(designations.get(5), "Suffix", "honorific", text, "", "Sr", "Jr", "I", "II", "III", "IV");

        assertEquals(6, designations.size());
    }

    @Test
    public void gender() {
        InputField gender = StandardInputFields.gender.createField(EN_US).build();
        assertField(gender, "Gender", "gender", text, "MALE", "FEMALE");
    }

    @Test
    public void age() {
        InputField gender = StandardInputFields.age.createField(EN_US).build();
        assertField(gender, "Age", "age", integer);
    }

    @Test
    public void address() {
        InputFieldGroup group = StandardInputFields.address.createGroup(EN_US).build();
        assertEquals("Address", group.getName());
        assertEquals("address", group.getShortCode());
        assertEquals("address", group.getId());
        List<InputFieldDesignator> designations = group.getFieldDesignations();
        assertFalse(designations.isEmpty());

        assertField(designations.get(0), "", "line1", text);
        assertField(designations.get(1), "", "line2", text);
        assertField(designations.get(2), "City", "city", text);
        assertField(designations.get(3), "State/Province", "territory", text);
        assertField(designations.get(4), "Postal Code", "postal-code", text);
        assertField(designations.get(5), "Country", "country", text, Locale.getISOCountries());

//        assertEquals(Arrays.asList(Locale.getISOCountries()).stream()
//                        .map(c->Locale.forLanguageTag("en_"+c).getDisplayCountry())
//                        .collect(Collectors.toList()),
//                designations.get(5).asField().get().getValues().get().stream()
//                        .map(InputField.Value::getDisplayName)
//                        .collect(Collectors.toList()));
    }

    @Test
    public void email() {
        InputField gender = StandardInputFields.email.createField(EN_US).build();
        assertField(gender, "Email Address", "email", text);
    }

    private void assertField(InputFieldDesignator designator,
                             String name,
                             String shortCode,
                             InputField.Type type,
                             String... expectedValues) {
        assertEquals(name, designator.getName());
        if (designator.getContainer() != null) {
            assertEquals(designator.getContainer().getId() + ":" + shortCode, designator.getId());
        } else {
            assertEquals(shortCode, designator.getId());
        }
        assertEquals(shortCode, designator.getShortCode());
        assertTrue(designator.asField().isPresent());
        Optional<List<InputField.Value>> foundValues = designator.asField().get().getValues();
        if (expectedValues != null && expectedValues.length > 0) {
            assertTrue(foundValues.isPresent());
            assertEquals(Arrays.asList(expectedValues),
                         foundValues.get().stream()
                                 .map(InputField.Value::getValue)
                                 .collect(Collectors.toList()));
        } else {
            assertFalse(foundValues.isPresent());
            assertEquals(type, designator.asField().get().getType());
        }
    }
}