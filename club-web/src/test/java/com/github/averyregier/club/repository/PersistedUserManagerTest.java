package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.LoginBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.view.UserBean;
import org.jooq.exception.DataAccessException;
import org.junit.Before;
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
    public static final String ANY_EMAIL = "any@email.com";

    private UserBean bean;
    private LoginBroker broker;
    private PersonManager personManager;
    private PersistedUserManager manager;

    @Before
    public void setup() {
        bean = new UserBean();
        bean.setProviderId(ANY_PROVIDER);
        bean.setUniqueId(ANY_ID);

        broker = mock(LoginBroker.class);
        personManager = mock(PersonManager.class);
        manager = new PersistedUserManager(personManager, () -> broker);
    }

    @Test
    public void getUserNotFound() {
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), any())).thenReturn(Optional.empty());

        assertFalse(manager.getUser(ANY_PROVIDER, ANY_ID).isPresent());
        assertFalse(manager.getUser(ANY_PROVIDER, ANY_ID).isPresent());

        verify(broker, times(2)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void getUserFoundAndCached() {
        User result = new User();

        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), any())).thenReturn(Optional.of(result));

        assertFound(result, ANY_PROVIDER, ANY_ID);
        assertFound(result, ANY_PROVIDER, ANY_ID);

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void syncUserFoundAndCachedWithNewAuth() {
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        User result = new User(person);

        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager))).thenReturn(Optional.of(result));

        bean.setEmail(ANY_EMAIL);
        assertSynced(result);
        assertEquals(ANY_EMAIL, person.getEmail().get());
        verify(personManager).sync(eq(result));

        String anotherEmail = "another@email.com";
        bean.setEmail(anotherEmail);
        assertSynced(result);
        assertEquals(anotherEmail, person.getEmail().get());
        verify(personManager, times(2)).sync(eq(result));

        verify(broker, times(1)).persist(eq(result.getLoginInformation()));
        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void syncUserFoundAndCachedWithExistingAuth() {
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        User result = new User(person);
        result.update(bean);
        result.resetAuth();

        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager))).thenReturn(Optional.of(result));

        bean.setEmail(ANY_EMAIL);
        assertSynced(result);
        assertEquals(ANY_EMAIL, person.getEmail().get());
        assertTrue(result.getLoginInformation().getAuth().isPresent());
        verify(personManager).sync(eq(result));

        assertSynced(result);

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }


    @Test
    public void createUserPersistedAndCached() {
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager))).thenReturn(Optional.empty());
        when(personManager.createPerson()).thenReturn(person);

        User user = manager.createUser(bean);
        assertEquals(person, user.getUpdater());
        assertEquals(ANY_PROVIDER, user.getLoginInformation().getProviderID());
        assertEquals(ANY_ID, user.getLoginInformation().getUniqueID());
        assertEquals(person.getId(), user.getLoginInformation().getID());
        assertFalse(user.getLoginInformation().getAuth().isPresent());

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verify(broker, times(1)).persist(eq(user.getLoginInformation()));
        verify(personManager).createPerson();

        assertFound(user, ANY_PROVIDER, ANY_ID);

        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void syncUserPersistedAndCached() {
        bean.setEmail(ANY_EMAIL);
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager))).thenReturn(Optional.empty());
        when(personManager.createPerson()).thenReturn(person);

        User user = manager.syncUser(bean);
        assertEquals(person, user.getUpdater());
        assertEquals(ANY_PROVIDER, user.getLoginInformation().getProviderID());
        assertEquals(ANY_ID, user.getLoginInformation().getUniqueID());
        assertEquals(person.getId(), user.getLoginInformation().getID());
        assertEquals(ANY_EMAIL, person.getEmail().get());

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verify(broker, times(1)).persist(eq(user.getLoginInformation()));
        verify(personManager).createPerson();

        assertFound(user, ANY_PROVIDER, ANY_ID);

        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void createUserWontCreateTwiceWhenFoundOnDb() {
        User result = new User();

        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager))).thenReturn(Optional.of(result));

        User user = manager.createUser(bean);
        assertEquals(result, user);

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());

        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void createUserWontCreateTwiceWhenAlreadyCached() {
        User result = new User();

        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), any())).thenReturn(Optional.of(result));

        assertFound(result, ANY_PROVIDER, ANY_ID);

        User user = manager.createUser(bean);
        assertEquals(result, user);

        verify(broker, times(1)).find(eq(ANY_PROVIDER), eq(ANY_ID), any());
        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void createUserWontCreateTwiceWhenErrorOnDb() {
        User result = new User();

        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager)))
                .thenReturn(Optional.empty(), Optional.of(result));
        when(personManager.createPerson()).thenReturn(person);
        doThrow(DataAccessException.class).when(broker).persist(any());

        User user = manager.createUser(bean);
        assertEquals(result, user);

        verify(broker, times(2)).find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager));
        verify(broker, times(1)).persist(argThat((a) -> matches((User.Login) a)));
        verify(personManager).createPerson();

        assertFound(user, ANY_PROVIDER, ANY_ID);

        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    @Test
    public void createUserFailsWhenDbIsDownOrInconsistent() {
        PersonAdapter person = new PersonAdapter(UUID.randomUUID().toString());
        when(broker.find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager)))
                .thenReturn(Optional.empty());
        when(personManager.createPerson()).thenReturn(person);
        doThrow(DataAccessException.class).when(broker).persist(any());

        try {
            User user = manager.createUser(bean);
            fail("Should have thrown an error");
        } catch(DataAccessException e) {
            //expected
        }

        verify(broker, times(2)).find(eq(ANY_PROVIDER), eq(ANY_ID), eq(personManager));
        verify(broker, times(1)).persist(argThat((a) -> matches((User.Login) a)));
        verify(personManager).createPerson();

        verifyNoMoreInteractions(broker);
        verifyNoMoreInteractions(personManager);
    }

    private boolean matches(User.Login a) {
        return a.getUniqueID().equals(ANY_ID) && a.getProviderID().equals(ANY_PROVIDER);
    }

    private void assertFound(User result, String provider, String userID) {
        Optional<User> user = manager.getUser(provider, userID);
        assertTrue(user.isPresent());
        assertEquals(result, user.get());
    }

    private void assertSynced(User result) {
        User user = manager.syncUser(bean);
        assertEquals(result, user);
        assertTrue(result.getLoginInformation().getAuth().isPresent());
    }
}