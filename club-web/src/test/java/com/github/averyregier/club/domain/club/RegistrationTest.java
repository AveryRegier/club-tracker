package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.utility.Action;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.view.UserBean;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by avery on 10/15/14.
 */
public class RegistrationTest {

    @Test
    public void userNamePrefilled() {
        User me = new User();
        me.setName("Foo", "Bar");
        Program program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
        RegistrationInformation registrationForm = program.createRegistrationForm(me);

        InputFieldGroup meFields = assertGroup("me", registrationForm.getForm(), 0);
        InputFieldGroup nameFields = assertGroup("name", meFields);

        assertEquals("Foo", registrationForm.getFields().get("me.name.given"));
        assertEquals("Bar", registrationForm.getFields().get("me.name.surname"));
//        Family family = me.asParent().get().register(registrationForm);
    }

    @Test
    public void genderPrefilled() {
        UserBean bean = new UserBean();
        bean.setGender("MALE");
        User me = new User();
        me.update(bean);
        Program program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
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
        Program program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
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
        InputFieldDesignator designator = form.get(form.size() - 1);
        assertEquals("action", designator.getShortCode());
        assertTrue(designator.asField().isPresent());
        assertEquals(InputField.Type.action, designator.asField().get().getType());
        assertEquals(Action.values().length, designator.asField().get().getValues().get().size());
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
    public void addSpouseAction() {
        User me = new User();
        Program program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
        RegistrationInformation registrationForm = program.createRegistrationForm(me);
        Map<String, String> values = new HashMap<>();
        values.put("action", Action.spouse.name());
        values.put("me.gender", Person.Gender.MALE.name());
        registrationForm = program.updateRegistrationForm(values);
        List<InputFieldDesignator> form = registrationForm.getForm();

        // verify the me fields didn't disappear
        InputFieldGroup meFields = assertGroup("me", registrationForm.getForm(), 0);
        InputFieldGroup nameFields = assertGroup("name", meFields);
        assertField("gender", meFields);
        assertField("email", meFields);

        assertEquals("MALE", registrationForm.getFields().get("me.gender"));

        int expected = Action.values().length - 1;
        if(expected > 0) {
            InputFieldDesignator designator = form.get(form.size() - 1);
            // you can add only one spouse, so make sure the button doesn't show up again.
            assertEquals("action", designator.getShortCode());
            assertTrue(designator.asField().isPresent());
            assertEquals(InputField.Type.action, designator.asField().get().getType());
            assertEquals(expected, designator.asField().get().getValues().get().size());

            for(InputField.Value value: designator.asField().get().getValues().get()) {
                assertNotEquals(Action.spouse.name(), value.getValue());
            }
        }

        assertNotEquals(values, registrationForm.getFields());
        assertNull(registrationForm.getFields().get("action"));

        InputFieldGroup spouseFields = assertGroup("spouse", registrationForm.getForm(), 1);
        assertGroup("name", spouseFields);
        assertField("gender", spouseFields);
        assertField("email", spouseFields);
        assertEquals("FEMALE", registrationForm.getFields().get("spouse.gender"));
    }

}
