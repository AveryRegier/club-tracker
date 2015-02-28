package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.adapter.MockClub;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Listener.LISTENER;
import static org.junit.Assert.assertEquals;

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
        assertEquals(person.getId(), new String(s.get(LISTENER.ID)));
    }

    private void assertPersonFields(Listener thing, StatementVerifier s) {
        s.assertUUID(thing.getClub(), LISTENER.CLUB_ID);
    }

    private Listener newListener() {
        String uuid = UUID.randomUUID().toString();
        PersonAdapter person = new PersonAdapter() {
            @Override
            public String getId() {
                return uuid;
            }
        };
        return new MockClub(null, new ProgramAdapter(null, null, null)).recruit(person);
    }

    private ListenerBroker setup(MockDataProvider provider) {
        return new ListenerBroker(mockConnector(provider));
    }
}