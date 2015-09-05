package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.FamilyBroker;
import com.github.averyregier.club.broker.PersonBroker;
import com.github.averyregier.club.broker.PersonRegistrationBroker;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import org.jooq.exception.DataAccessException;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by avery on 7/8/15.
 */
public class PersistedPersonManagerTest {
    public static final String ANY_ID = UUID.randomUUID().toString();
    public static final String ANY_EMAIL = "any@email.com";

    private PersonBroker personBroker;
    private FamilyBroker familyBroker;
    private PersonRegistrationBroker registrationBroker;
    private PersistedPersonManager manager;

    @Before
    public void setup() {
        personBroker = mock(PersonBroker.class);
        registrationBroker = mock(PersonRegistrationBroker.class);
        manager = new PersistedPersonManager(
                () -> personBroker,
                () -> familyBroker,
                () -> registrationBroker);
    }

    @Test
    public void getPersonNotFound() {

        when(personBroker.find(eq(ANY_ID))).thenReturn(Optional.empty());

        assertFalse(manager.lookup(ANY_ID).isPresent());
        assertFalse(manager.lookup(ANY_ID).isPresent());

        verify(personBroker, times(2)).find(eq(ANY_ID));
        verifyNoMoreInteractions(personBroker);
    }

    @Test
    public void getPersonFoundAndCached() {
        PersonAdapter person = new PersonAdapter(ANY_ID);
        when(personBroker.find(eq(ANY_ID))).thenReturn(Optional.of(person));

        assertTrue(manager.lookup(ANY_ID).isPresent());
        assertTrue(manager.lookup(ANY_ID).isPresent());

        verify(personBroker, times(1)).find(eq(ANY_ID));
        verifyNoMoreInteractions(personBroker);
    }

    @Test
    public void getPersonFails() {
        when(personBroker.find(eq(ANY_ID)))
                .thenThrow(DataAccessException.class);

        try {
            manager.lookup(ANY_ID).isPresent();
            fail("should have thrown an exception");
        } catch(DataAccessException e)  {
            // expected
        }

        verify(personBroker, times(1)).find(eq(ANY_ID));
        verifyNoMoreInteractions(personBroker);
    }

    @Test
    public void createPersonCreatesAndCaches() {
        Person person = manager.createPerson();

        assertEquals(person, manager.lookup(person.getId()).orElse(null));

        verify(personBroker, times(1)).persist(eq(person));
        verifyNoMoreInteractions(personBroker);
    }

    @Test
    public void syncPersonAlreadyCached() {
        // TODO: There's an issue with this simple implementation.
        // If another JVM updates the db between caching and
        // syncing, then those changes get overwritten.
        // To fix it, though, we need to change the PersonBroker
        // (and probably all the others)
        PersonAdapter person = setupCachedPerson();

        manager.sync(person);

        verify(personBroker, times(1)).persist(eq(person));
        verifyNoMoreInteractions(personBroker);
    }

    private PersonAdapter setupCachedPerson() {
        PersonAdapter person = new PersonAdapter(ANY_ID);
        when(personBroker.find(eq(ANY_ID))).thenReturn(Optional.of(person));
        assertEquals(person, manager.lookup(person.getId()).orElse(null));
        verify(personBroker, times(1)).find(eq(ANY_ID));
        return person;
    }
}