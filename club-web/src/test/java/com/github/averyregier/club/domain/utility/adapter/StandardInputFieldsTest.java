package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.LocaleTinkerer;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Address;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.NameBuilder;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.utility.Action;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.averyregier.club.TestUtility.assertEmpty;
import static com.github.averyregier.club.TestUtility.getUser;
import static com.github.averyregier.club.domain.utility.InputField.Type.integer;
import static com.github.averyregier.club.domain.utility.InputField.Type.text;
import static com.github.averyregier.club.domain.utility.UtilityMethods.optMap;
import static org.junit.Assert.*;

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
        // http://notes.ericwillis.com/2009/11/common-name-prefixes-titles-and-honorifics/
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

        User person = new User();
        person.getUpdater().setName(new NameBuilder()
                .given("Space")
                .surname("Alien")
                .middle("J")
                .friendly("Spacey")
                .title("Dr")
                .honorific("III")
                .build());
        Map<String, String> model = group.map(person);
        assertEquals("Space", model.get("given"));
        assertEquals("Alien", model.get("surname"));
        assertEquals("J", model.get("middle"));
        assertEquals("III", model.get("honorific"));
        assertEquals("Dr", model.get("title"));
        assertEquals("Spacey", model.get("friendly"));

        User user = new User();
        group.update(user, o);
        assertEquals(o, user.getName());
    }

    @Test
    public void childName() {
        InputFieldGroup group = StandardInputFields.childName.createGroup(Locale.forLanguageTag("en_US")).build();
        assertEquals("Name", group.getName());
        assertEquals("childName", group.getShortCode());
        assertEquals("childName", group.getId());
        List<InputFieldDesignator> designations = group.getFieldDesignations();
        assertFalse(designations.isEmpty());

        assertField(designations.get(0), "Given", "given", text);
        assertField(designations.get(1), "Middle", "middle", text);
        assertField(designations.get(2), "Surname", "surname", text);
        assertField(designations.get(3), "Friendly", "friendly", text);
        assertField(designations.get(4), "Suffix", "honorific", text, "", "Jr", "I", "II", "III", "IV");

        assertEquals(5, designations.size());

        HashMap<String, String> input = new HashMap<>();
        input.put("given", "Space");
        input.put("surname", "Alien");
        input.put("middle", "J");
        input.put("friendly", "Spacey");
        input.put("honorific", "III");

        Object o = group.validate(input).get();
        assertTrue(o instanceof Name);
        Name name = (Name)o;
        assertEquals("Space", name.getGivenName());
        assertEquals("Alien", name.getSurname());
        assertEquals("J", name.getMiddleNames().get(0));
        assertEquals("III", name.getHonorificName());
        assertEquals("Spacey", name.getFriendlyName());

        User person = new User();
        person.getUpdater().setName(new NameBuilder()
                .given("Space")
                .surname("Alien")
                .middle("J")
                .friendly("Spacey")
                .honorific("III")
                .build());
        Map<String, String> model = group.map(person);
        assertEquals("Space", model.get("given"));
        assertEquals("Alien", model.get("surname"));
        assertEquals("J", model.get("middle"));
        assertEquals("III", model.get("honorific"));
        assertEquals("Spacey", model.get("friendly"));

        User user = new User();
        group.update(user, o);
        assertEquals(o, user.getName());
    }

    @Test
    public void gender() {
        InputField gender = StandardInputFields.gender.createField(EN_US).build();
        assertField(gender, "Gender", "gender", InputField.Type.gender, "MALE", "FEMALE");
        assertEquals(Person.Gender.MALE, gender.validate("MALE").get());
        assertEquals(Person.Gender.FEMALE, gender.validate("FEMALE").get());
        assertFalse(gender.validate("Both").isPresent());

        assertEquals("FEMALE", gender.map(getUser(b -> b.setGender("FEMALE"))));
        assertEquals("MALE", gender.map(getUser(b -> b.setGender("MALE"))));

        assertNull(gender.map(new User()));

        User user = new User();
        gender.update(user, Person.Gender.FEMALE);
        assertEquals(Person.Gender.FEMALE, user.getGender().get());
    }

    @Test
    public void age() {
        InputField age = StandardInputFields.age.createField(EN_US).build();
        assertField(age, "Age", "age", integer);
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
        assertFieldIgnoreValues(designations.get(5), "Country", "country", text);

        assertEquals(LocaleTinkerer.getAllCountryDropDown("en", Locale.getDefault()).stream()
                        .map(InputField.Value::getDisplayName)
                        .collect(Collectors.toList()),
                designations.get(5).asField().get().getValues().get().stream()
                        .map(InputField.Value::getDisplayName)
                        .collect(Collectors.toList()));

        assertEquals(LocaleTinkerer.getAllCountryDropDown("en", Locale.getDefault()).stream()
                        .map(InputField.Value::getValue)
                        .collect(Collectors.toList()),
                designations.get(5).asField().get().getValues().get().stream()
                        .map(InputField.Value::getValue)
                        .collect(Collectors.toList()));

        assertEmpty(group.map(new User()));

        HashMap<String, String> input = new HashMap<>();
        input.put("line1", "123 A St.");
        input.put("line2", "Apt 4");
        input.put("city", "Clubville");
        input.put("territory", "AS");
        input.put("postal-code", "12345");
        input.put("country", "US");

        Object o = group.validate(input).get();
        assertTrue(o instanceof Address);
        Address address = (Address)o;
        assertEquals("123 A St.", address.getLine1());
        assertEquals("Apt 4", address.getLine2());
        assertEquals("Clubville", address.getCity());
        assertEquals("AS", address.getTerritory());
        assertEquals("12345", address.getPostalCode());
        assertEquals("US", address.getCountry().getValue());

        User user = new User();
        group.update(user, o);
        assertEquals(o, optMap(user.getFamily(), Family::getAddress).orElse(null));

        User person = new User();
        person.getUpdater().setAddress(address);
        Map<String, String> model = group.map(person);
        assertEquals("123 A St.", model.get("line1"));
        assertEquals("Apt 4", model.get("line2"));
        assertEquals("Clubville", model.get("city"));
        assertEquals("AS", model.get("territory"));
        assertEquals("12345", model.get("postal-code"));
        assertEquals("US", model.get("country"));
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

        User user = new User();
        email.update(user, "hi@there.com");
        assertEquals("hi@there.com", user.getEmail().get());
    }

    @Test
    public void grade() {
        InputField field = StandardInputFields.ageGroup.createField(EN_US).build();
        assertField(field, "Age Group", "ageGroup", InputField.Type.ageGroup,
                Arrays.asList(AgeGroup.DefaultAgeGroup.values()).stream()
                        .map(Enum::name)
                        .toArray(String[]::new));
        for(AgeGroup.DefaultAgeGroup group: AgeGroup.DefaultAgeGroup.values()) {
            assertEquals(group, field.validate(group.name()).get());
        }
        assertFalse(field.validate("Ageless").isPresent());
        assertEquals("THIRD_GRADE", field.map(new ClubberAdapter() {
            @Override
            public AgeGroup getCurrentAgeGroup() {
                return AgeGroup.DefaultAgeGroup.THIRD_GRADE;
            }
        }));
        assertEquals("COLLEGE", field.map(new ClubberAdapter() {
            @Override
            public AgeGroup getCurrentAgeGroup() {
                return AgeGroup.DefaultAgeGroup.COLLEGE;
            }
        }));
        assertNull(field.map(new User()));

        User person = new User();
        field.update(person, AgeGroup.DefaultAgeGroup.FIRST_GRADE);
        assertEquals(AgeGroup.DefaultAgeGroup.FIRST_GRADE, person.getCurrentAgeGroup());
    }

    @Test
    public void action() {
        InputField field = StandardInputFields.action.createField(EN_US).build();
        assertField(field, "Action", "action", InputField.Type.action,
                Arrays.asList(Action.values()).stream()
                        .map(Enum::name)
                        .toArray(String[]::new));
        for(Action group: Action.values()) {
            assertEquals(group, field.validate(group.name()).get());
        }
        assertFalse(field.validate("Exterminate!").isPresent());
    }

    private void assertField(InputFieldDesignator designator,
                             String name,
                             String shortCode,
                             InputField.Type type,
                             String... expectedValues) {
        assertFieldIgnoreValues(designator, name, shortCode, type);

        Optional<List<InputField.Value>> foundValues = designator.asField().get().getValues();
        if (expectedValues != null && expectedValues.length > 0) {
            assertTrue(foundValues.isPresent());
            assertEquals(Arrays.asList(expectedValues),
                         foundValues.get().stream()
                                 .map(InputField.Value::getValue)
                                 .collect(Collectors.toList()));
        } else {
            assertFalse(foundValues.isPresent());
        }
    }

    private void assertFieldIgnoreValues(InputFieldDesignator designator, String name, String shortCode, InputField.Type type) {
        assertEquals(name, designator.getName());
        if (designator.getContainer() != null) {
            assertEquals(designator.getContainer().getId() + "." + shortCode, designator.getId());
        } else {
            assertEquals(shortCode, designator.getId());
        }
        assertEquals(shortCode, designator.getShortCode());
        assertTrue(designator.asField().isPresent());
        assertEquals(type, designator.asField().get().getType());
    }
}