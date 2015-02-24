package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.program.AgeGroup;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Person.PERSON;
import static org.junit.Assert.*;

public class PersonBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final PersonAdapter person = newPerson();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(1)
                .statement(StatementType.MERGE, (s) -> assertUUID(person, s))
                .statement(StatementType.UPDATE, (s) -> assertNullFields(s, 0, 6))
                .statement(StatementType.INSERT, (s) -> {
                    assertUUID(person, s);
                    assertNullFields(s, 1, 7);
                })
                .build();

        setup(provider).persist(person);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final PersonAdapter person = newPerson();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(person);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final PersonAdapter person = newPerson();
        person.setGender(Person.Gender.MALE);
        person.setAgeGroup(AgeGroup.DefaultAgeGroup.THIRD_GRADE);
        person.setEmail("some.person@somewhere.com");
        person.setName(new Name() {
            @Override
            public String getGivenName() {
                return "First";
            }

            @Override
            public String getSurname() {
                return "Last";
            }

            @Override
            public List<String> getMiddleNames() {
                return Arrays.asList("Middle");
            }

            @Override
            public Optional<String> getTitle() {
                return Optional.of("Dr");
            }

            @Override
            public String getFriendlyName() {
                return "Friend";
            }

            @Override
            public String getHonorificName() {
                return "III";
            }

            @Override
            public String getFullName() {
                fail("full name isn't persisted");
                return null;
            }
        });

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(1)
                .statement(StatementType.MERGE, (s) -> assertUUID(person, s))
                .statement(StatementType.UPDATE, (s) -> assertPersonFields(person, s))
                .statement(StatementType.INSERT, (s) -> {
                    assertUUID(person, s);
                    assertPersonFields(person, s);
                })
                .build();

        setup(provider).persist(person);
    }

    private void assertUUID(PersonAdapter person, StatementVerifier s) {
        assertEquals(person.getId(), new String(s.get(PERSON.ID)));
    }

    private void assertNullFields(StatementVerifier s, int start, int end) {
        for (int i = start; i <= end; i++) {
            assertNull(Integer.toString(i), s.get(i));
        }
    }

    private void assertPersonFields(PersonAdapter thing, StatementVerifier s) {
        assertEquals(thing.getGender().get().name(), s.get(PERSON.GENDER));
        assertEquals(thing.getName().getFriendlyName(), s.get(PERSON.FRIENDLY));
        assertEquals(thing.getName().getGivenName(), s.get(PERSON.GIVEN));
        assertEquals(thing.getName().getSurname(), s.get(PERSON.SURNAME));
        assertEquals(thing.getName().getHonorificName(), s.get(PERSON.HONORIFIC));
        assertEquals(thing.getName().getTitle().get(), s.get(PERSON.TITLE));
        assertEquals(thing.getEmail().get(), s.get(PERSON.EMAIL));
    }

    private PersonAdapter newPerson() {
        String uuid = UUID.randomUUID().toString();
        return new PersonAdapter() {
            @Override
            public String getId() {
                return uuid;
            }
        };
    }

    private PersonBroker setup(MockDataProvider provider) {
        return new PersonBroker(mockConnector(provider));
    }
}