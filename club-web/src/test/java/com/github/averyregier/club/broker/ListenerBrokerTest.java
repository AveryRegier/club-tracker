package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ListenerRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.MockClub;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.Programs;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Listener.LISTENER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ListenerBrokerTest {

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Listener listener = newListener();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(listener);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final Listener listener = newListener();

        MockDataProvider provider = mergeProvider(assertUUID(listener), assertFields(listener));

        setup(provider).persist(listener);
    }


    private Consumer<StatementVerifier> assertUUID(Listener person) {
        return (s) -> assertUUID(person, s);
    }

    private Consumer<StatementVerifier> assertFields(Listener person) {
        return (s) -> assertPersonFields(person, s);
    }

    private void assertUUID(Listener person, StatementVerifier s) {
        s.assertUUID(person.getId(), LISTENER.ID);
    }

    private void assertPersonFields(Listener thing, StatementVerifier s) {
        s.assertUUID(thing.getClub(), LISTENER.CLUB_ID);
    }

    private Listener newListener() {
        String uuid = UUID.randomUUID().toString();
        PersonAdapter person = new PersonAdapter(uuid);
        return new MockClub(null, new ProgramAdapter()).recruit(person);
    }

    private ListenerBroker setup(MockDataProvider provider) {
        return new ListenerBroker(mockConnector(provider));
    }

    @Test
    public void findNoListener() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        ClubManager clubManager = new ClubManager();

        MockDataProvider provider = select((s) -> {
            s.assertUUID(person.getId(), LISTENER.ID);
        }, (create) -> create.newResult(LISTENER));

        assertFalse(setup(provider).find(person.getId(), personManager, clubManager).isPresent());

        assertFalse(person.asClubLeader().isPresent());
    }

    @Test
    public void findListener() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        ClubManager clubManager = new ClubManager();
        Club club = clubManager.createClub(null, Programs.AWANA.get());

        MockDataProvider provider = selectOne((s) -> {
            s.assertUUID(person.getId(), LISTENER.ID);
        }, LISTENER, r -> {
            r.setId(person.getId().getBytes());
            r.setClubId(club.getId().getBytes());
        });

        Listener listener = setup(provider).find(person.getId(), personManager, clubManager).get();
        assertListener(person, listener);
    }

    @Test
    public void findClubListeners() {
        PersonManager personManager = new PersonManager();
        Person person1 = personManager.createPerson();
        Person person2 = personManager.createPerson();
        ClubManager clubManager = new ClubManager();
        Club club = clubManager.createClub(null, Programs.AWANA.get());

        MockDataProvider provider = select((s) -> {
            s.assertUUID(club.getId(), LISTENER.CLUB_ID);
        }, (create) -> {
            Result<ListenerRecord> result = create.newResult(LISTENER);
            addClubListenerRecord(person1, club, create, result);
            addClubListenerRecord(person2, club, create, result);
            return result;
        });

        Set<Listener> listeners = setup(provider).find(club, personManager);
        Iterator<Listener> iterator = listeners.iterator();
        assertListener(person1, iterator.next());
        assertListener(person2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    private void assertListener(Person person1, Listener listener) {
        assertEquals(listener, person1.asListener().get());
        assertEquals(person1.getId(), listener.getId());
    }

    private void addClubListenerRecord(Person person, Club club, DSLContext create, Result<ListenerRecord> result) {
        ListenerRecord record = create.newRecord(LISTENER);
        result.add(record);
        record.setClubId(club.getId().getBytes());
        record.setId(person.getId().getBytes());
    }

}