package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.adapter.MockClub;
import com.github.averyregier.club.domain.club.adapter.MockLeader;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Leader.LEADER;

public class LeaderBrokerTest {

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final ClubLeader leader = newLeader();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(leader);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final ClubLeader leader = newLeader();

        MockDataProvider provider = mergeProvider(assertUUID(leader), assertFields(leader));

        setup(provider).persist(leader);
    }


    private Consumer<StatementVerifier> assertUUID(ClubLeader person) {
        return (s) -> assertUUID(person, s);
    }

    private Consumer<StatementVerifier> assertFields(ClubLeader person) {
        return (s) -> assertPersonFields(person, s);
    }

    private void assertUUID(ClubLeader person, StatementVerifier s) {
        s.assertUUID(person.getId(), LEADER.ID);
    }

    private void assertPersonFields(ClubLeader thing, StatementVerifier s) {
        s.assertFieldEquals(thing.getLeadershipRole().name(), LEADER.ROLE);
        s.assertUUID(thing.getClub(), LEADER.CLUB_ID);
    }

    private ClubLeader newLeader() {
        String uuid = UUID.randomUUID().toString();
        PersonAdapter person = new PersonAdapter() {
            @Override
            public String getId() {
                return uuid;
            }
        };
        return new MockLeader(person, ClubLeader.LeadershipRole.random(), new MockClub(null, null));
    }

    private LeaderBroker setup(MockDataProvider provider) {
        return new LeaderBroker(mockConnector(provider));
    }
}