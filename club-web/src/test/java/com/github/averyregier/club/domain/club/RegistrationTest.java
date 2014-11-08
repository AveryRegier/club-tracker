package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.utility.Action;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.view.UserBean;
import org.junit.Test;

import java.util.List;

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


}
