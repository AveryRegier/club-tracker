package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.utility.Action;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.view.UserBean;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by avery on 10/15/14.
 */
public class RegistrationFormTest {
    private Program program;
    
    @Before
    public void setup() {
        program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
    }

    @Test
    public void userNamePrefilled() {
        User me = new User();
        me.setName("Foo", "Bar");
        
        RegistrationInformation registrationForm = program.createRegistrationForm(me);

        InputFieldGroup meFields = assertGroup("me", registrationForm.getForm(), 0);
        InputFieldGroup nameFields = assertGroup("name", meFields);

        assertEquals("Foo", registrationForm.getFields().get("me.name.given"));
        assertEquals("Bar", registrationForm.getFields().get("me.name.surname"));
    }

    @Test
    public void genderPrefilled() {
        UserBean bean = new UserBean();
        bean.setGender("MALE");
        User me = new User();
        me.update(bean);
        RegistrationInformation registrationForm = program.createRegistrationForm(me);

        InputFieldGroup meFields = assertGroup("me", registrationForm.getForm(), 0);
        InputFieldGroup nameFields = assertGroup("name", meFields);
        assertField("gender", meFields);

        assertNull(registrationForm.getFields().get("me.name.given"));
        assertNull(registrationForm.getFields().get("me.name.surname"));

        assertEquals("MALE", registrationForm.getFields().get("me.gender"));
    }

    @Test
    public void emailPrefilled() {
        UserBean bean = new UserBean();
        bean.setEmail("hi@there.com");
        User me = new User();
        me.update(bean);
        RegistrationInformation registrationForm = program.createRegistrationForm(me);

        InputFieldGroup meFields = assertGroup("me", registrationForm.getForm(), 0);
        InputFieldGroup nameFields = assertGroup("name", meFields);
        assertField("email", meFields);

        assertNull(registrationForm.getFields().get("me.name.given"));
        assertNull(registrationForm.getFields().get("me.name.surname"));

        assertNull(registrationForm.getFields().get("me.gender"));
        assertEquals("hi@there.com", registrationForm.getFields().get("me.email"));
    }

    @Test
    public void standardActions() {
        User me = new User();
        Program program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
        RegistrationInformation registrationForm = program.createRegistrationForm(me);
        List<InputFieldDesignator> form = registrationForm.getForm();

        assertActionField(form, Action.values().length);
    }

    private InputFieldGroup assertGroup(String shortCode, List<InputFieldDesignator> form, int index) {
        assertTrue(!form.isEmpty());
        InputFieldDesignator meFields = form.get(index);
        assertEquals(shortCode, meFields.getShortCode());
        assertTrue(meFields.asGroup().isPresent());
        return meFields.asGroup().get();
    }

    private InputFieldGroup assertGroup(String shortCode, InputFieldGroup parent) {
        assertTrue(!parent.getFieldDesignations().isEmpty());
        InputFieldDesignator meFields = parent.find(shortCode).get();
        assertEquals(shortCode, meFields.getShortCode());
        assertTrue(meFields.asGroup().isPresent());
        return meFields.asGroup().get();
    }

    private InputField assertField(String shortCode, List<InputFieldDesignator> form, int index) {
        assertTrue(!form.isEmpty());
        InputFieldDesignator meFields = form.get(index);
        assertEquals(shortCode, meFields.getShortCode());
        assertTrue(meFields.asField().isPresent());
        return meFields.asField().get();
    }

    private InputField assertField(String shortCode, InputFieldGroup parent) {
        assertTrue(!parent.getFieldDesignations().isEmpty());
        InputFieldDesignator meFields = parent.find(shortCode).get();
        assertEquals(shortCode, meFields.getShortCode());
        assertTrue(meFields.asField().isPresent());
        return meFields.asField().get();
    }

    @Test
    public void addSpouseOfGuy() {
        Person.Gender meGender = Person.Gender.MALE;
        assertDefaultSpouse(meGender);
    }

    @Test
    public void addSpouseOfGal() {
        Person.Gender meGender = Person.Gender.FEMALE;
        assertDefaultSpouse(meGender);
    }

    private void assertDefaultSpouse(Person.Gender meGender) {
        Map<String, String> values = new HashMap<>();
        values.put("action", Action.spouse.name());
        values.put("me.gender", meGender.name());
        values.put("me.name.surname", "Smith");
        RegistrationInformation registrationForm = program.updateRegistrationForm(values);
        List<InputFieldDesignator> form = registrationForm.getForm();

        // verify the me fields didn't disappear
        assertPersonFieldsPresent(registrationForm, "me", 0);

        assertEquals(meGender.name(), registrationForm.getFields().get("me.gender"));

        assertAllButSpouseAction(form);

        assertNotEquals(values, registrationForm.getFields());
        assertNull(registrationForm.getFields().get("action"));

        assertPersonFieldsPresent(registrationForm, "spouse", 1);
        assertEquals(meGender.opposite().name(), registrationForm.getFields().get("spouse.gender"));
        assertEquals("Smith", registrationForm.getFields().get("spouse.name.surname"));
    }

    private void assertAllButSpouseAction(List<InputFieldDesignator> form) {
        int expected = Action.values().length - 1;
        if(expected > 0) {
            InputFieldDesignator designator = assertActionField(form, expected);

            for(InputField.Value value: designator.asField().get().getValues().get()) {
                assertNotEquals(Action.spouse.name(), value.getValue());
            }
        }
    }

    private InputFieldDesignator assertActionField(List<InputFieldDesignator> form, int expected) {
        InputFieldDesignator designator = form.get(form.size() - 1);
        // you can add only one spouse, so make sure the button doesn't show up again.
        assertEquals("action", designator.getShortCode());
        assertTrue(designator.asField().isPresent());
        assertEquals(InputField.Type.action, designator.asField().get().getType());
        assertEquals(expected, designator.asField().get().getValues().get().size());
        return designator;
    }

    private void assertPersonFieldsPresent(RegistrationInformation registrationForm, String id, int index) {
        InputFieldGroup meFields = assertGroup(id, registrationForm.getForm(), index);
        assertGroup("name", meFields);
        assertField("gender", meFields);
        assertField("email", meFields);
    }

    private void assertChildFieldsPresent(RegistrationInformation registrationForm, String id, int index) {
        InputFieldGroup meFields = assertGroup(id, registrationForm.getForm(), index);
        assertGroup("childName", meFields);
        assertField("gender", meFields);
        assertField("email", meFields);
        assertField("ageGroup", meFields);
    }

    @Test
    public void addChildAction() {
        Map<String, String> values = new HashMap<>();
        values.put("action", Action.child.name());
        values.put("me.name.surname", "Smith");
        RegistrationInformation registrationForm = program.updateRegistrationForm(values);
        List<InputFieldDesignator> form = registrationForm.getForm();

        // verify the me fields didn't disappear
        assertPersonFieldsPresent(registrationForm, "me", 0);
        assertEquals("Smith", registrationForm.getFields().get("me.name.surname"));

        assertAllActions(form);

        assertChildFieldsPresent(registrationForm, "child1", 1);
        assertEquals("Smith", registrationForm.getFields().get("child1.childName.surname"));
        assertFalse(registrationForm.getFields().containsKey("child1.childName.title"));
    }

    @Test
    public void addChildActionWithSpouse() {
        Map<String, String> values = new HashMap<>();
        values.put("action", Action.child.name());
        values.put("me.name.surname", "Smith");
        values.put("spouse.name.surname", "Another");

        RegistrationInformation registrationForm = program.updateRegistrationForm(values);
        List<InputFieldDesignator> form = registrationForm.getForm();

        // verify the me fields didn't disappear
        assertPersonFieldsPresent(registrationForm, "me", 0);
        assertEquals("Smith", registrationForm.getFields().get("me.name.surname"));

        // verify the spouse fields didn't disappear
        assertPersonFieldsPresent(registrationForm, "spouse", 1);
        assertEquals("Another", registrationForm.getFields().get("spouse.name.surname"));

        assertChildFieldsPresent(registrationForm, "child1", 2);
        assertEquals("Smith", registrationForm.getFields().get("child1.childName.surname"));

        assertAllButSpouseAction(form);
    }

    @Test
    public void addSpouseActionWithChild() {
        Map<String, String> values = new HashMap<>();
        values.put("action", Action.spouse.name());
        values.put("me.name.surname", "Smith");
        values.put("child1.childName.surname", "Another");

        RegistrationInformation registrationForm = program.updateRegistrationForm(values);
        List<InputFieldDesignator> form = registrationForm.getForm();

        // verify the me fields didn't disappear
        assertPersonFieldsPresent(registrationForm, "me", 0);
        assertEquals("Smith", registrationForm.getFields().get("me.name.surname"));

        // verify the spouse fields didn't appear where they are supposed to
        assertPersonFieldsPresent(registrationForm, "spouse", 1);
        assertEquals("Smith", registrationForm.getFields().get("spouse.name.surname"));

        // verify the child fields didn't disappear
        assertChildFieldsPresent(registrationForm, "child1", 2);
        assertEquals("Another", registrationForm.getFields().get("child1.childName.surname"));

        assertAllButSpouseAction(form);
    }

    @Test
    public void addSecondChild() {
        Map<String, String> values = new HashMap<>();
        values.put("action", Action.child.name());
        values.put("me.name.surname", "Smith");
        values.put("child1.childName.surname", "Another");
        RegistrationInformation registrationForm = program.updateRegistrationForm(values);
        List<InputFieldDesignator> form = registrationForm.getForm();

        // verify the me fields didn't disappear
        assertPersonFieldsPresent(registrationForm, "me", 0);
        assertEquals("Smith", registrationForm.getFields().get("me.name.surname"));

        assertAllActions(form);

        assertChildFieldsPresent(registrationForm, "child1", 1);
        assertEquals("Another", registrationForm.getFields().get("child1.childName.surname"));

        assertChildFieldsPresent(registrationForm, "child2", 2);
        assertEquals("Smith", registrationForm.getFields().get("child2.childName.surname"));
    }

    private void assertAllActions(List<InputFieldDesignator> form) {
        InputFieldDesignator designator = assertActionField(form, Action.values().length);
        int i = 0;
        for(InputField.Value value: designator.asField().get().getValues().get()) {
            assertEquals(Action.values()[i++].name(), value.getValue());
        }
    }

    @Test
    public void existingFamilyUpdateRegistration() {
        Map<String, String> values = new HashMap<>();
        values.put("me.name.surname", "Smith");
        values.put("spouse.name.surname", "Smith");
        values.put("child1.childName.surname", "Another");
        values.put("child2.childName.surname", "Smith");

        RegistrationInformation firstForm = program.updateRegistrationForm(values);
        User user = new User();
        Family family = firstForm.register(user);
        RegistrationInformation newForm = program.createRegistrationForm(user);

        Map<String, String> fields = newForm.getFields();
        assertEquals("Smith", fields.get("me.name.surname"));
        assertEquals("Smith", fields.get("spouse.name.surname"));
        assertEquals("Another", fields.get("child1.childName.surname"));
        assertEquals("Smith", fields.get("child2.childName.surname"));

        assertAllButSpouseAction(newForm.getForm());
        Family updatedFamily = newForm.register(user);

        assertEquals(family, user.getFamily().get());
        assertEquals(family, updatedFamily);
        assertEquals(2, family.getClubbers().size());
        assertEquals(2, family.getParents().size());
    }
    @Test
    public void existingFamilyChangedRegistration() {
        Map<String, String> values = new HashMap<>();
        values.put("me.name.surname", "Smith");

        RegistrationInformation firstForm = program.updateRegistrationForm(values);
        User user = new User();
        Family family = firstForm.register(user);
        RegistrationInformation newForm = program.createRegistrationForm(user);

        HashMap<String, String> newValues = new HashMap<>(newForm.getFields());

        newValues.put("spouse.name.surname", "Smith");
        newValues.put("child1.childName.surname", "Another");
        newValues.put("child2.childName.surname", "Smith");

        RegistrationInformation lastForm = program.updateRegistrationForm(newValues);
        Family updatedFamily = lastForm.register(user);

        assertEquals(family, user.getFamily().get());
        assertEquals(family, updatedFamily);
        assertEquals(2, family.getClubbers().size());
        assertEquals(2, family.getParents().size());
    }
}
