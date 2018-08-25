package com.github.averyregier.club.broker;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Programs;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Clubber.CLUBBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClubberBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final Clubber clubber = newClubber();

        MockDataProvider provider = mergeProvider(assertUUID(clubber), assertNullFields());

        setup(provider, null, null).persist(clubber);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Clubber clubber = newClubber();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider, null, null).persist(clubber);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final ClubberAdapter clubber = newClubber();
        ProgramAdapter program = new ProgramAdapter();
        new FamilyAdapter(clubber, program);
        clubber.setClub(new MockClub(null, program));
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.FIFTH_GRADE);

        MockDataProvider provider = mergeProvider(assertUUID(clubber), assertFields(clubber));

        setup(provider, null, null).persist(clubber);
    }


    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(
                CLUBBER.AGE_GROUP, CLUBBER.CLUB_ID, CLUBBER.FAMILY_ID);
    }

    private Consumer<StatementVerifier> assertUUID(Clubber person) {
        return (s) -> assertUUID(person, s);
    }

    private Consumer<StatementVerifier> assertFields(Clubber person) {
        return (s) -> assertClubberFields(person, s);
    }

    private void assertUUID(Clubber person, StatementVerifier s) {
        s.assertUUID(person.getId(), CLUBBER.ID);
    }

    private void assertClubberFields(Clubber clubber, StatementVerifier s) {
        s.assertFieldEquals(clubber.getCurrentAgeGroup().name(), CLUBBER.AGE_GROUP);
        s.assertUUID(clubber.getClub(), CLUBBER.CLUB_ID);
        s.assertUUID(clubber.getFamily(), CLUBBER.FAMILY_ID);
    }

    private ClubberAdapter newClubber() {
        String uuid = UUID.randomUUID().toString();
        return new ClubberAdapter(new PersonAdapter(uuid));
    }

    private ClubberBroker setup(MockDataProvider provider, PersonManager personManager, ClubManager clubManager) {
        ClubFactory factory = mock(ClubFactory.class);
        when(factory.getClubManager()).thenReturn(clubManager);
        when(factory.getPersonManager()).thenReturn(personManager);
        when(factory.getConnector()).thenReturn(mockConnector(provider));
        return new ClubberBroker(factory);
    }

    @Test
    public void findClubber() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        String familyId = UUID.randomUUID().toString();
        ClubManager clubManager = new ClubManager();
        Club club = clubManager.createClub(null, Programs.AWANA.get());

        MockDataProvider provider = selectOne((s) -> {
            s.assertUUID(person.getId(), CLUBBER.ID);
        }, CLUBBER, (r)-> {
            r.setId(person.getId().getBytes());
            r.setFamilyId(familyId.getBytes());
            r.setClubId(club.getId().getBytes());
            r.setAgeGroup(AgeGroup.DefaultAgeGroup.THIRD_GRADE.name());
        });

        Clubber clubber = setup(provider, personManager, clubManager).find(person.getId()).get();
        assertEquals(person.getId(), clubber.getId());
        assertEquals(AgeGroup.DefaultAgeGroup.THIRD_GRADE, person.asClubber().get().getCurrentAgeGroup());
        assertEquals(familyId, person.getFamily().get().getId());
    }

    @Test
    public void findClubberNoFamily() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        ClubManager clubManager = new ClubManager();
        Club club = clubManager.createClub(null, Programs.AWANA.get());

        MockDataProvider provider = selectOne((s) -> {
            s.assertUUID(person.getId(), CLUBBER.ID);
        }, CLUBBER, (r)-> {
            r.setId(person.getId().getBytes());
            r.setFamilyId(null);
            r.setClubId(club.getId().getBytes());
            r.setAgeGroup(AgeGroup.DefaultAgeGroup.THIRD_GRADE.name());
        });

        Clubber clubber = setup(provider, personManager, clubManager).find(person.getId()).get();
        assertEquals(person.getId(), clubber.getId());
        assertEquals(AgeGroup.DefaultAgeGroup.THIRD_GRADE, person.asClubber().get().getCurrentAgeGroup());

        assertFalse(clubber.getFamily().isPresent());
    }


    @Test
    public void findClubberNoClub() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        ClubManager clubManager = new ClubManager();

        MockDataProvider provider = selectOne((s) -> {
            s.assertUUID(person.getId(), CLUBBER.ID);
        }, CLUBBER, (r)-> {
            r.setId(person.getId().getBytes());
            r.setFamilyId(null);
            r.setClubId(null);
            r.setAgeGroup(AgeGroup.DefaultAgeGroup.THIRD_GRADE.name());
        });

        Clubber clubber = setup(provider, personManager, clubManager).find(person.getId()).get();
        assertEquals(person.getId(), clubber.getId());

        assertFalse(clubber.getClub().isPresent());
    }


    @Test
    public void findNoClubber() {
        PersonManager personManager = new PersonManager();
        Person person = personManager.createPerson();
        ClubManager clubManager = new ClubManager();

        MockDataProvider provider = select((s) -> {
            s.assertUUID(person.getId(), CLUBBER.ID);
        }, (create)->create.newResult(CLUBBER));

        assertFalse(setup(provider, personManager, clubManager).find(person.getId()).isPresent());

        assertFalse(person.asClubber().isPresent());
    }
}