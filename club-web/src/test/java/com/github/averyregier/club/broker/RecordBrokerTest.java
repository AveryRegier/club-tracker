package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.club.Signing;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.MockClub;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Record.RECORD;

public class RecordBrokerTest {

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

        record.sign(new MockClub(null, new ProgramAdapter(null, null, null)).recruit(record.getClubber()), "Well Done!");

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
        s.assertFieldEquals(new java.sql.Date(signing.getDate().toEpochDay()), RECORD.SIGN_DATE);
    }

    private ClubberRecord newRecord() {
        String uuid = UUID.randomUUID().toString();
        return new ClubberAdapter(new PersonAdapter() {
            @Override
            public String getId() {
                return uuid;
            }
        }).getRecord(Optional.of(TnTCurriculum.get()
                .getBooks().get(0)
                .getSectionGroups().get(0)
                .getSections().get(0))).get();
    }

    private ClubberRecordBroker setup(MockDataProvider provider) {
        return new ClubberRecordBroker(mockConnector(provider));
    }
}