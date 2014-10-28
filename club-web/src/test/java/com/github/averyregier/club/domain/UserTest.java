package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.view.UserBean;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void gender() {
        User classUnderTest = new User();
        UserBean bean = new UserBean();
        bean.setGender("MALE");
        classUnderTest.update(bean);
        assertEquals(Person.Gender.MALE, classUnderTest.getGender().get());
    }

    @Test
    public void noGender() {
        User classUnderTest = new User();
        UserBean bean = new UserBean();
        classUnderTest.update(bean);
        assertEquals(Optional.empty(), classUnderTest.getGender());
    }

    @Test
    public void noEmail() {
        User classUnderTest = new User();
        UserBean bean = new UserBean();
        classUnderTest.update(bean);
        assertEquals(Optional.empty(), classUnderTest.getEmail());

    }

    @Test
    public void email() {
        User classUnderTest = new User();
        UserBean bean = new UserBean();
        bean.setEmail("blubber@flubber.org");
        classUnderTest.update(bean);
        assertEquals("blubber@flubber.org", classUnderTest.getEmail().get());
    }
}