package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.MockClub;
import com.github.averyregier.club.domain.club.adapter.MockLeader;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.program.Programs;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Leader.LEADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        s.assertUUID(person.getClub(), LEADER.CLUB_ID);
    }

    private void assertPersonFields(ClubLeader thing, StatementVerifier s) {
        s.assertFieldEquals(thing.getLeadershipRole().name(), LEADER.ROLE);
    }

    private ClubLeader newLeader() {
        String uuid = UUID.randomUUID().toString();
        PersonAdapter person = new PersonAdapter(uuid);
        return new MockLeader(person, ClubLeader.LeadershipRole.random(), new MockClub(null, null));
    }

    private LeaderBroker setup(MockDataProvider provider) {
        return new LeaderBroker(mockConnector(provider));
    }

    @Test
    public void findNoLeader() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        ClubManager clubManager = new ClubManager();

        MockDataProvider provider = select((s) -> {
            s.assertUUID(person.getId(), LEADER.ID);
        }, (create)->create.newResult(LEADER));

        assertFalse(setup(provider).find(person.getId(), personManager, clubManager).isPresent());

        assertFalse(person.asClubLeader().isPresent());
    }

    @Test
    public void findLeader() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        ClubManager clubManager = new ClubManager();
        Club club = clubManager.createClub(null, Programs.AWANA.get());

        MockDataProvider provider = selectOne((s) -> {
            s.assertUUID(person.getId(), LEADER.ID);
        }, LEADER, r -> {
            r.setId(person.getId().getBytes());
            r.setClubId(club.getId().getBytes());
            r.setRole(ClubLeader.LeadershipRole.DIRECTOR.name());
        });

        ClubLeader clubLeader = setup(provider).find(person.getId(), personManager, clubManager).get();
        assertEquals(clubLeader, person.asClubLeader().get());
        assertEquals(person.getId(), clubLeader.getId());
        assertEquals(ClubLeader.LeadershipRole.DIRECTOR, clubLeader.getLeadershipRole());
    }
}