package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.FamilyAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Family.FAMILY;

/**
 * Created by avery on 2/25/15.
 */
public class FamilyBrokerTest {
    @Test
    public void testPersist() {
        final Family family = newFamily();

        setup(mergeProvider((s) -> s.assertUUID(family, FAMILY.ID))).persist(family);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Family family = newFamily();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(family);
    }

    private Family newFamily() {
        return new FamilyAdapter(new ClubberAdapter(new PersonAdapter()));
    }

    private Broker<Family> setup(MockDataProvider provider) {
        return new FamilyBroker(mockConnector(provider));
    }
}
