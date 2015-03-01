package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.adapter.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Clubber.CLUBBER;
import static org.junit.Assert.assertEquals;

public class ClubberBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final Clubber clubber = newClubber();

        MockDataProvider provider = mergeProvider(assertUUID(clubber), assertNullFields());

        setup(provider).persist(clubber);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Clubber clubber = newClubber();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(clubber);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final ClubberAdapter clubber = newClubber();
        new FamilyAdapter(clubber);
        clubber.setClub(new MockClub(null, new ProgramAdapter(null, null, null)));
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.FIFTH_GRADE);

        MockDataProvider provider = mergeProvider(assertUUID(clubber), assertFields(clubber));

        setup(provider).persist(clubber);
    }


    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(
                CLUBBER.AGE_GROUP, CLUBBER.CLUB_ID, CLUBBER.FAMILY_ID);
    }

    private Consumer<StatementVerifier> assertUUID(Clubber person) {
        return (s) -> assertUUID(person, s);
    }

    private Consumer<StatementVerifier> assertFields(Clubber person) {
        return (s) -> assertClubberFields(person, s);
    }

    private void assertUUID(Clubber person, StatementVerifier s) {
        assertEquals(person.getId(), new String(s.get(CLUBBER.ID)));
    }

    private void assertClubberFields(Clubber clubber, StatementVerifier s) {
        s.assertFieldEquals(clubber.getCurrentAgeGroup().name(), CLUBBER.AGE_GROUP);
        s.assertUUID(clubber.getClub(), CLUBBER.CLUB_ID);
        s.assertUUID(clubber.getFamily(), CLUBBER.FAMILY_ID);
    }

    private ClubberAdapter newClubber() {
        String uuid = UUID.randomUUID().toString();
        return new ClubberAdapter(new PersonAdapter() {
            @Override
            public String getId() {
                return uuid;
            }
        });
    }

    private ClubberBroker setup(MockDataProvider provider) {
        return new ClubberBroker(mockConnector(provider));
    }
}