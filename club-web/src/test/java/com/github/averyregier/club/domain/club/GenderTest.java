package com.github.averyregier.club.domain.club;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GenderTest {

    @Test
    public void opposite() {
        assertEquals(Person.Gender.FEMALE, Person.Gender.MALE.opposite());
        assertEquals(Person.Gender.MALE, Person.Gender.FEMALE.opposite());
    }

}