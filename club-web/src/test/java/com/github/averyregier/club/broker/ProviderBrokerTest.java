package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ProviderRecord;
import com.github.averyregier.club.domain.login.Provider;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.broker.BrokerTestUtil.select;
import static com.github.averyregier.club.db.tables.Provider.PROVIDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProviderBrokerTest {

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Provider authProvider = newProvider();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(authProvider);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final Provider authProvider = newProvider();

        MockDataProvider provider = mergeProvider(assertKey(authProvider), assertFields(authProvider));

        setup(provider).persist(authProvider);
    }

    private Consumer<StatementVerifier> assertKey(Provider person) {
        return (s) -> assertKey(person, s);
    }

    private Consumer<StatementVerifier> assertFields(Provider person) {
        return (s) -> assertProviderFields(person, s);
    }

    private void assertKey(Provider thing, StatementVerifier s) {
        s.assertFieldEquals(thing.getId(), PROVIDER.PROVIDER_ID);
    }

    private void assertProviderFields(Provider thing, StatementVerifier s) {
        s.assertFieldEquals(thing.getName(), PROVIDER.NAME);
        s.assertFieldEquals(thing.getImage(), PROVIDER.IMAGE);
        s.assertFieldEquals(thing.getSite(), PROVIDER.SITE);
        s.assertFieldEquals(thing.getClientKey(), PROVIDER.CLIENT_KEY);
        s.assertFieldEquals(thing.getSecret(), PROVIDER.SECRET);
    }

    private Provider newProvider() {
        return new Provider("AN ID", "A NAME", "AN IMAGE", "A SITE", "A CLIENT KEY", "A SECRET");
    }

    private ProviderBroker setup(MockDataProvider provider) {
        return new ProviderBroker(mockConnector(provider));
    }

    @Test
    public void findNoProviders() {
        MockDataProvider provider = select((s) -> {
            // TODO: find a way to assert empty where clause
        }, (r) -> r.newResult(PROVIDER));

        assertTrue(setup(provider).find().isEmpty());
    }

    @Test
    public void findAllProviders() {
        List<ProviderRecord> allRecords = Arrays.asList(new ProviderRecord(
                "providerId",
                "site",
                "name",
                "image",
                "clientKey",
                "secret"
        ));

        MockDataProvider provider = select((s) -> {
            // TODO: find a way to assert empty where clause
        }, (r) -> {
            Result<ProviderRecord> result = r.newResult(PROVIDER);
            result.addAll(allRecords);
            return result;
        });

        List<Provider> providers = setup(provider).find();

        assertEquals(1, providers.size());

        int i=0;
        for(Provider p: providers) {
            ProviderRecord record = allRecords.get(i++);
            assertEquals(record.getClientKey(), p.getClientKey());
            assertEquals(record.getImage(), p.getImage());
            assertEquals(record.getName(), p.getName());
            assertEquals(record.getProviderId(), p.getId());
            assertEquals(record.getSite(), p.getSite());
            assertEquals(record.getSecret(), p.getSecret());
        }
    }
}