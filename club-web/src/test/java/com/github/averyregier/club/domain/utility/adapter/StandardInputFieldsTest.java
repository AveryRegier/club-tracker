package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.InputField.Type.integer;
import static com.github.averyregier.club.domain.utility.InputField.Type.text;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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

        HashMap<String, String> input = new HashMap<>();
        input.put("given", "Space");
        input.put("surname", "Alien");
        input.put("middle", "J");
        input.put("friendly", "Spacey");
        input.put("title", "Dr");
        input.put("honorific", "III");

        Object o = group.validate(input).get();
        assertTrue(o instanceof Name);
        Name name = (Name)o;
        assertEquals("Dr", name.getTitle().get());
        assertEquals("Space", name.getGivenName());
        assertEquals("Alien", name.getSurname());
        assertEquals("J", name.getMiddleNames().get(0));
        assertEquals("III", name.getHonorificName());
        assertEquals("Spacey", name.getFriendlyName());
    }

    @Test
    public void gender() {
        InputField gender = StandardInputFields.gender.createField(EN_US).build();
        assertField(gender, "Gender", "gender", InputField.Type.gender, "MALE", "FEMALE");
        assertEquals(Person.Gender.MALE, gender.validate("MALE").get());
        assertEquals(Person.Gender.FEMALE, gender.validate("FEMALE").get());
        assertFalse(gender.validate("Both").isPresent());
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
        InputField email = StandardInputFields.email.createField(EN_US).build();
        assertField(email, "Email Address", "email", InputField.Type.email);

        assertEquals("a@b.c", email.validate("a@b.c").get());
        assertFalse(email.validate("http://i.am.a.url").isPresent());
        assertFalse(email.validate("spaces aren@t.happy").isPresent());
        assertEquals("dots.are@.ha.py", email.validate("dots.are@.ha.py").get());
        assertFalse(email.validate("notdotsaren@thappy").isPresent());
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