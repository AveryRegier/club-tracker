package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Parent;
import com.github.averyregier.club.domain.club.adapter.FamilyAdapter;
import com.github.averyregier.club.domain.club.adapter.MockParent;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Parent.PARENT;
import static org.junit.Assert.assertEquals;

public class ParentBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final Parent parent = newParent();

        MockDataProvider provider = mergeProvider(assertUUID(parent), assertNullFields());

        setup(provider).persist(parent);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Parent parent = newParent();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(parent);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final Parent parent = newParent();
        new FamilyAdapter(parent);

        MockDataProvider provider = mergeProvider(assertUUID(parent), assertFields(parent));

        setup(provider).persist(parent);
    }


    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(PARENT.FAMILY_ID);
    }

    private Consumer<StatementVerifier> assertUUID(Parent person) {
        return (s) -> assertUUID(person, s);
    }

    private Consumer<StatementVerifier> assertFields(Parent person) {
        return (s) -> assertParentFields(person, s);
    }

    private void assertUUID(Parent person, StatementVerifier s) {
        assertEquals(person.getId(), new String(s.get(PARENT.ID)));
    }

    private void assertParentFields(Parent parent, StatementVerifier s) {
        s.assertUUID(parent.getFamily(), PARENT.FAMILY_ID);
    }

    private Parent newParent() {
        String uuid = UUID.randomUUID().toString();
        return new MockParent(new PersonAdapter() {
            @Override
            public String getId() {
                return uuid;
            }
        });
    }

    private ParentBroker setup(MockDataProvider provider) {
        return new ParentBroker(mockConnector(provider));
    }
}