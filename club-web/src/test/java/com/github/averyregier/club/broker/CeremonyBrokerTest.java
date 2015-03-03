package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Ceremony;
import com.github.averyregier.club.domain.club.adapter.CeremonyAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Ceremony.CEREMONY;

public class CeremonyBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final Ceremony ceremony = new CeremonyAdapter(null);

        MockDataProvider provider = mergeProvider(assertPrimaryKey(ceremony), assertNullFields());

        setup(provider).persist(ceremony);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Ceremony ceremony = new CeremonyAdapter();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(ceremony);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final Ceremony ceremony = new CeremonyAdapter();

        MockDataProvider provider = mergeProvider(assertPrimaryKey(ceremony), assertFields(ceremony));

        setup(provider).persist(ceremony);
    }


    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(
                CEREMONY.NAME, CEREMONY.PRESENTATION_DATE);
    }

    private Consumer<StatementVerifier> assertPrimaryKey(Ceremony ceremony) {
        return (s) -> assertPrimaryKey(ceremony, s);
    }

    private Consumer<StatementVerifier> assertFields(Ceremony ceremony) {
        return (s) -> assertClubberFields(ceremony, s);
    }

    private void assertPrimaryKey(Ceremony ceremony, StatementVerifier s) {
        s.assertUUID(ceremony, CEREMONY.ID);
    }

    private void assertClubberFields(Ceremony ceremony, StatementVerifier s) {
        s.assertFieldEquals(ceremony.getName(), CEREMONY.NAME);
        s.assertFieldEquals(new java.sql.Date(ceremony.presentationDate().toEpochDay()), CEREMONY.PRESENTATION_DATE);
    }

    private CeremonyBroker setup(MockDataProvider provider) {
        return new CeremonyBroker(mockConnector(provider));
    }
}