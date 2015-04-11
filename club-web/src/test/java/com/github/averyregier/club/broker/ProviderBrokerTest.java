package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.login.Provider;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Provider.PROVIDER;

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
}