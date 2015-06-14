package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.RecordRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.club.Signing;
import com.github.averyregier.club.domain.club.adapter.*;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Record.RECORD;
import static org.junit.Assert.*;

public class ClubberRecordBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final ClubberRecord record = newRecord();

        MockDataProvider provider = mergeProvider(assertPrimaryKey(record), assertNullFields());

        setup(provider).persist(record);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final ClubberRecord record = newRecord();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(record);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final ClubberRecord record = newRecord();

        record.sign(new MockClub(null, new ProgramAdapter()).recruit(record.getClubber()), "Well Done!");

        MockDataProvider provider = mergeProvider(assertPrimaryKey(record), assertFields(record));

        setup(provider).persist(record);
    }


    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(
                RECORD.SIGN_DATE, RECORD.CLUB_ID, RECORD.SIGNED_BY, RECORD.NOTE);
    }

    private Consumer<StatementVerifier> assertPrimaryKey(ClubberRecord person) {
        return (s) -> assertPrimaryKey(person, s);
    }

    private Consumer<StatementVerifier> assertFields(ClubberRecord person) {
        return (s) -> assertClubberFields(person, s);
    }

    private void assertPrimaryKey(ClubberRecord record, StatementVerifier s) {
        s.assertUUID(record.getClubber().getId(), RECORD.CLUBBER_ID);
        s.assertFieldEquals(record.getSection().getId(), RECORD.SECTION_ID);
    }

    private void assertClubberFields(ClubberRecord record, StatementVerifier s) {
        Signing signing = record.getSigning().get();
        s.assertUUID(signing.by().getClub(), RECORD.CLUB_ID);
        s.assertUUID(signing.by().getId(), RECORD.SIGNED_BY);
        s.assertFieldEquals(signing.getNote(), RECORD.NOTE);
        s.assertFieldEquals(UtilityMethods.toSqlDate(signing.getDate()), RECORD.SIGN_DATE);
    }

    private ClubberRecord newRecord() {
        String uuid = UUID.randomUUID().toString();
        return new ClubberAdapter(new PersonAdapter(uuid))
            .getRecord(Optional.of(TnTCurriculum.get()
                .getBooks().get(0)
                .getSectionGroups().get(0)
                .getSections().get(0))).get();
    }

    private ClubberRecordBroker setup(MockDataProvider provider) {
        return new ClubberRecordBroker(mockConnector(provider));
    }

    @Test
    public void findNoClubberRecords() {

        Clubber mockClubber = new ClubberAdapter(new PersonManager().createPerson());

        MockDataProvider provider = select((s) -> {
            s.assertUUID(mockClubber.getId(), RECORD.CLUBBER_ID);
            s.assertUUID(mockClubber.getClub().get().getId(), RECORD.CLUB_ID);
        }, (r) -> r.newResult(RECORD));

        assertTrue(setup(provider).find(mockClubber, new PersonManager()).isEmpty());
    }

    @Test
    public void findClubberRecords() {
        PersonManager personManager = new PersonManager();
        ClubberAdapter mockClubber = new ClubberAdapter(personManager.createPerson());
        Club club = new ClubManager().createClub(null, TnTCurriculum.get());
        mockClubber.setClub((ClubAdapter)club);
        String firstSection = TnTCurriculum.get().getBooks().get(0).getSections().get(0).getId();
        String secondSection = TnTCurriculum.get().getBooks().get(0).getSections().get(1).getId();
        ListenerAdapter listener = new ListenerAdapter(personManager.createPerson());
        listener.setClubGroup(club);
        List<RecordRecord> recordList = new ArrayList<>(2);
        recordList.add(new RecordRecord(
                mockClubber.getId().getBytes(),
                club.getId().getBytes(),
                firstSection,
                null, // signed by
                null, //date
                null // note
        ));
        LocalDate localDate = LocalDate.now().minusYears(1);
        recordList.add(new RecordRecord(
                mockClubber.getId().getBytes(),
                club.getId().getBytes(),
                secondSection,
                listener.getId().getBytes(), // signed by
                UtilityMethods.toSqlDate(localDate), //date
                "Well done!" // note
        ));

        MockDataProvider provider = select((s) -> {
            s.assertUUID(mockClubber.getId(), RECORD.CLUBBER_ID);
            s.assertUUID(mockClubber.getClub().get().getId(), RECORD.CLUB_ID);
        }, (r) -> {
            Result<RecordRecord> result = r.newResult(RECORD);
            result.addAll(recordList);
            return result;
        });

        Collection<ClubberRecord> records = setup(provider).find(mockClubber, personManager);
        assertEquals(2, records.size());
        int i=0;
        for(ClubberRecord record: records) {
            RecordRecord rr = recordList.get(i++);

            assertEquals(mockClubber, record.getClubber());
            assertEquals(rr.getSectionId(), record.getSection().getId());
            Optional<Signing> signing = record.getSigning();
            if(rr.getSignedBy() == null) {
                assertFalse(signing.isPresent());
            } else {
                assertTrue(signing.isPresent());
                assertEquals(new String(rr.getSignedBy()), signing.get().by().getId());
                assertEquals(rr.getNote(), signing.get().getNote());
                assertEquals(rr.getSignDate().toLocalDate(), signing.get().getDate());
            }
        }
    }
}