package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.LoginBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import org.jooq.exception.DataAccessException;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by avery on 7/3/15.
 */
public class PersistedUserManagerTest {

    public static final String ANY_ID = "an_id";
    public static final String ANY_PROVIDER = "provider";

    @Test
    public void getUserNotFound() {
        LoginBroker broker = mock(LoginBroker.class);
        PersistedUserManager manager = new PersistedUserManager(() -> broker);
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), any())).thenReturn(Optional.empty());

        assertFalse(manager.getUser(ANY_PROVIDER, ANY_ID).isPresent());
        assertFalse(manager.getUser(ANY_PROVIDER, ANY_ID).isPresent());

        verify(broker, times(2)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
    }

    @Test
    public void getUserFoundAndCached() {
        User result = new User();

        LoginBroker broker = mock(LoginBroker.class);
        PersistedUserManager manager = new PersistedUserManager(() -> broker);
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), any())).thenReturn(Optional.of(result));

        assertFound(result, manager, ANY_PROVIDER, ANY_ID);
        assertFound(result, manager, ANY_PROVIDER, ANY_ID);

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
    }

    @Test
    public void createUserPersistedAndCached() {
        LoginBroker broker = mock(LoginBroker.class);
        PersonManager personManager = mock(PersonManager.class);
        PersistedUserManager manager = new PersistedUserManager(personManager, () -> broker);
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager))).thenReturn(Optional.empty());
        when(personManager.createPerson()).thenReturn(person);

        User user = manager.createUser(ANY_PROVIDER, ANY_ID);
        assertEquals(person, user.getUpdater());
        assertEquals(ANY_PROVIDER, user.getLoginInformation().getProviderID());
        assertEquals(ANY_ID, user.getLoginInformation().getUniqueID());
        assertEquals(person.getId(), user.getLoginInformation().getID());
        assertFalse(user.getLoginInformation().getAuth().isPresent());

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verify(broker, times(1)).persist(eq(user.getLoginInformation()));
        verify(personManager).createPerson();

        assertFound(user, manager, ANY_PROVIDER, ANY_ID);

        verifyNoMoreInteractions(broker);
    }

    @Test
    public void createUserWontCreateTwiceWhenFoundOnDb() {
        User result = new User();

        LoginBroker broker = mock(LoginBroker.class);
        PersonManager personManager = mock(PersonManager.class);
        PersistedUserManager manager = new PersistedUserManager(personManager, () -> broker);
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager))).thenReturn(Optional.of(result));

        User user = manager.createUser(ANY_PROVIDER, ANY_ID);
        assertEquals(result, user);

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());

        verifyNoMoreInteractions(broker);
    }

    @Test
    public void createUserWontCreateTwiceWhenAlreadyCached() {
        User result = new User();

        LoginBroker broker = mock(LoginBroker.class);
        PersistedUserManager manager = new PersistedUserManager(() -> broker);
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), any())).thenReturn(Optional.of(result));

        assertFound(result, manager, ANY_PROVIDER, ANY_ID);

        User user = manager.createUser(ANY_PROVIDER, ANY_ID);
        assertEquals(result, user);

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
    }

    @Test
    public void createUserWontCreateTwiceWhenErrorOnDb() {
        User result = new User();

        LoginBroker broker = mock(LoginBroker.class);
        PersonManager personManager = mock(PersonManager.class);
        PersistedUserManager manager = new PersistedUserManager(personManager, () -> broker);
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager)))
                .thenReturn(Optional.empty(), Optional.of(result));
        when(personManager.createPerson()).thenReturn(person);
        doThrow(DataAccessException.class).when(broker).persist(any());

        User user = manager.createUser(ANY_PROVIDER, ANY_ID);
        assertEquals(result, user);

        verify(broker, times(2)).find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager));
        verify(broker, times(1)).persist(argThat((a) -> matches((User.Login) a)));
        verify(personManager).createPerson();

        assertFound(user, manager, ANY_PROVIDER, ANY_ID);

        verifyNoMoreInteractions(broker);
    }

    @Test
    public void createUserFailsWhenDbIsDownOrInconsistent() {
        LoginBroker broker = mock(LoginBroker.class);
        PersonManager personManager = mock(PersonManager.class);
        PersistedUserManager manager = new PersistedUserManager(personManager, () -> broker);
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager)))
                .thenReturn(Optional.empty());
        when(personManager.createPerson()).thenReturn(person);
        doThrow(DataAccessException.class).when(broker).persist(any());

        try {
            User user = manager.createUser(ANY_PROVIDER, ANY_ID);
            fail("Should have thrown an error");
        } catch(DataAccessException e) {
            //expected
        }

        verify(broker, times(2)).find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager));
        verify(broker, times(1)).persist(argThat((a) -> matches((User.Login) a)));
        verify(personManager).createPerson();

        verifyNoMoreInteractions(broker);
    }

    private boolean matches(User.Login a) {
        return a.getUniqueID().equals(ANY_ID) && a.getProviderID().equals(ANY_PROVIDER);
    }

    private void assertFound(User result, PersistedUserManager manager, String provider, String userID) {
        Optional<User> user = manager.getUser(provider, userID);
        assertTrue(user.isPresent());
        assertEquals(result, user.get());
    }
}