package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.view.UserBean;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

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

        InputFieldGroup meFields = assertGroup("me", registrationForm.getForm());
        InputFieldGroup nameFields = assertGroup("name", meFields);

        assertEquals("Foo", registrationForm.getFields().get(nameFields.find("given").get()));
        assertEquals("Bar", registrationForm.getFields().get(nameFields.find("surname").get()));
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

        InputFieldGroup meFields = assertGroup("me", registrationForm.getForm());
        InputFieldGroup nameFields = assertGroup("name", meFields);

        assertNull(registrationForm.getFields().get(nameFields.find("given").get()));
        assertNull(registrationForm.getFields().get(nameFields.find("surname").get()));

        assertEquals(Person.Gender.MALE, registrationForm.getFields().get(meFields.find("gender").get()));
    }

    private InputFieldGroup assertGroup(String shortCode, List<InputFieldDesignator> form) {
        assertTrue(!form.isEmpty());
        InputFieldDesignator meFields = form.get(0);
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

    private Optional<InputField> find(String shortCode, List<InputFieldDesignator> fieldDesignations) {
        for(InputFieldDesignator d: fieldDesignations) {
            if(d.getShortCode().equals(shortCode)) {
                return d.asField();
            }
        }
        return Optional.empty();
    }
}
