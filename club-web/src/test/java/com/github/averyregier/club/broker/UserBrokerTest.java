package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.LoginRecord;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.view.UserBean;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Login.LOGIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UserBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final User person = newUser();

        MockDataProvider provider = mergeProvider(assertUUID(person), assertNullFields());

        setup(provider).persist(person.getLoginInformation());
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final User person = newUser();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(person.getLoginInformation());
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final User person = newUser();
        person.resetAuth();

        MockDataProvider provider = mergeProvider(assertUUID(person), assertFields(person));

        setup(provider).persist(person.getLoginInformation());
    }


    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(LOGIN.AUTH);
    }

    private Consumer<StatementVerifier> assertUUID(User person) {
        return (s) -> assertUUID(person, s);
    }

    private Consumer<StatementVerifier> assertFields(User person) {
        return (s) -> assertPersonFields(person, s);
    }

    private void assertUUID(User person, StatementVerifier s) {
        s.assertUUID(person.getId(), LOGIN.ID);
        s.assertFieldEquals(person.getLoginInformation().getProviderID(), LOGIN.PROVIDER_ID);
        s.assertFieldEquals(person.getLoginInformation().getUniqueID(), LOGIN.UNIQUE_ID);
    }

    private void assertPersonFields(User thing, StatementVerifier s) {
        s.assertFieldEquals(thing.getLoginInformation().getAuth().get(), LOGIN.AUTH);
    }

    private User newUser() {
        String uuid = UUID.randomUUID().toString();
        User user = new User(new PersonAdapter(uuid));
        UserBean bean = new UserBean();
        bean.setProviderId("foo");
        bean.setUniqueId(UUID.randomUUID().toString());
        user.update(bean);
        return user;
    }

    private LoginBroker setup(MockDataProvider provider) {
        return new LoginBroker(mockConnector(provider));
    }

    @Test
    public void testFindsByUniqueId() {
        String providerId = "Someone";
        String uniqueID = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();

        MockDataProvider proovider = new MockDataProviderBuilder().statement(StatementType.SELECT, (s)->{
            s.assertFieldEquals(providerId, LOGIN.PROVIDER_ID);
            s.assertFieldEquals(uniqueID, LOGIN.UNIQUE_ID);
        }).reply((create)->{
            Result<LoginRecord> result = create.newResult(LOGIN);
            result.add(create.newRecord(LOGIN));
            result.get(0).setValue(LOGIN.ID, id.getBytes());
            result.get(0).setValue(LOGIN.AUTH, null);
            return result;
        }).build();

        User user = setup(proovider).find(providerId, uniqueID).get();

        assertEquals(id, user.getId());
        assertFalse(user.getLoginInformation().getAuth().isPresent());
        assertEquals(providerId, user.getLoginInformation().getProviderID());
        assertEquals(uniqueID, user.getLoginInformation().getUniqueID());
    }

}