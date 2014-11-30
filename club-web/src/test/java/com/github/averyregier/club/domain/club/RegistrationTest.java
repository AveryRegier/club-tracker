package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.github.averyregier.club.domain.utility.UtilityMethods.asLinkedSet;
import static com.github.averyregier.club.domain.utility.UtilityMethods.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by avery on 11/21/14.
 */
public class RegistrationTest {
    private Program program;

    @Before
    public void setup() {
        program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
    }

    @Test
    public void firstTest() {
        User me = new User();
        Map<String, String> formValues =
                 map("me.name.given", "Green")
                .put("me.name.surname", "Flubber")
                .build();
        RegistrationInformation form = program.updateRegistrationForm(formValues);
        Family family = me.register(form);
        assertNotNull(family);
        assertEquals(asLinkedSet(me), family.getParents());
        assertEquals("Green", me.getName().getGivenName());
        assertEquals("Flubber", me.getName().getSurname());
    }
}
