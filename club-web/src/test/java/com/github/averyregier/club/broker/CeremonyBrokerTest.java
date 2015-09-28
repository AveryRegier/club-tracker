package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Ceremony;
import com.github.averyregier.club.domain.club.adapter.CeremonyAdapter;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Ceremony.CEREMONY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        s.assertFieldEquals(UtilityMethods.toSqlDate(ceremony.presentationDate()), CEREMONY.PRESENTATION_DATE);
    }

    private CeremonyBroker setup(MockDataProvider provider) {
        return new CeremonyBroker(mockConnector(provider));
    }

    @Test
    public void findCeremony() {
        String ceremonyId = UUID.randomUUID().toString();
        LocalDate now = LocalDate.now();

        MockDataProvider provider = selectOne((s) -> s.assertUUID(ceremonyId, CEREMONY.ID), CEREMONY, (r) -> {
            r.setId(ceremonyId.getBytes());
            r.setName("Foobar");
            r.setPresentationDate(UtilityMethods.toSqlDate(now));
        });
        Optional<Ceremony> ceremony = setup(provider).find(ceremonyId);

        assertTrue(ceremony.isPresent());
        assertEquals("Foobar", ceremony.get().getName());
        assertEquals(ceremonyId, ceremony.get().getId());
        assertEquals(now, ceremony.get().presentationDate());
    }
}