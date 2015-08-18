package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Address;
import com.github.averyregier.club.domain.club.adapter.AddressAdapter;
import com.github.averyregier.club.domain.utility.adapter.CountryValue;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Address.ADDRESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AddressBrokerTest {

    @Before
    public void setup() {
    }


    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Address address = newAddress();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(address);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final Address address = newAddress();

        MockDataProvider provider = mergeProvider(assertPrimaryKey(address), assertFields(address));

        setup(provider).persist(address);
    }


    private Consumer<StatementVerifier> assertPrimaryKey(Address address) {
        return (s) -> assertPrimaryKey(address, s);
    }

    private Consumer<StatementVerifier> assertFields(Address address) {
        return (s) -> {
            s.assertFieldEquals(address.getLine1(), ADDRESS.LINE1);
            s.assertFieldEquals(address.getLine2(), ADDRESS.LINE2);
            s.assertFieldEquals(address.getCity(), ADDRESS.CITY);
            s.assertFieldEquals(address.getPostalCode(), ADDRESS.POSTAL_CODE);
            s.assertFieldEquals(address.getCountry().getValue(), ADDRESS.COUNTRY);
        };
    }

    private void assertPrimaryKey(Address address, StatementVerifier s) {
        s.assertUUID(address.getId(), ADDRESS.ID);
    }


    private Address newAddress() {
        String uuid = UUID.randomUUID().toString();
        return new AddressAdapter("100 Whatever St.", "Apt foo", "Somewhere", "4564", "AS",
                new CountryValue(Locale.US, Locale.getDefault()));
    }

    private AddressBroker setup(MockDataProvider provider) {
        return new AddressBroker(mockConnector(provider));
    }

    @Test
    public void findNoAddress() {
        String id = UUID.randomUUID().toString();
        MockDataProvider provider = select((s) -> {
            s.assertUUID(id, ADDRESS.ID);
        }, (r) -> r.newResult(ADDRESS));

        assertFalse(setup(provider).find(id).isPresent());
    }

    @Test
    public void findAddress() {
        String id = UUID.randomUUID().toString();
        MockDataProvider provider = selectOne((s) -> {
            s.assertUUID(id, ADDRESS.ID);
        }, ADDRESS, (r) -> {
            r.setId(id.getBytes());
            r.setLine1("Line 1");
            r.setLine2("Line 2");
            r.setCity("A City");
            r.setPostalCode("12345");
            r.setTerritory("NE");
            r.setCountry("US");
        });

        Address address = setup(provider).find(id).get();
        assertEquals(id, address.getId());
        assertEquals("Line 1", address.getLine1());
        assertEquals("Line 2", address.getLine2());
        assertEquals("A City", address.getCity());
        assertEquals("12345", address.getPostalCode());
        assertEquals("US", address.getCountry().getValue());
    }
}