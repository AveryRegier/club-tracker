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
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Person.PERSON;
import static org.junit.Assert.*;

public class PersonBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final PersonAdapter person = newPerson();

        MockDataProvider provider = mergeProvider(assertUUID(person), assertNullFields());

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

        MockDataProvider provider = mergeProvider(assertUUID(person), assertFields(person));

        setup(provider).persist(person);
    }


    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(
                PERSON.GENDER, PERSON.FRIENDLY, PERSON.GIVEN, PERSON.SURNAME,
                PERSON.HONORIFIC, PERSON.TITLE, PERSON.EMAIL);
    }

    private Consumer<StatementVerifier> assertUUID(PersonAdapter person) {
        return (s) -> assertUUID(person, s);
    }

    private Consumer<StatementVerifier> assertFields(PersonAdapter person) {
        return (s) -> assertPersonFields(person, s);
    }

    private void assertUUID(PersonAdapter person, StatementVerifier s) {
        assertEquals(person.getId(), new String(s.get(PERSON.ID)));
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