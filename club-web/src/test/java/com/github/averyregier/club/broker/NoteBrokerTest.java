package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Note;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.NoteAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Note.NOTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NoteBrokerTest {

    @Test
    public void testPersistMerges() throws Exception {
        final Note note = new NoteAdapter(getPerson(), "A note");

        MockDataProvider provider = mergeProvider(assertPrimaryKey(note), assertNullFields());

        setup(provider).persist(note);
    }

    private Person getPerson() {
        String uuid = UUID.randomUUID().toString();
        Person mock = mock(Person.class);
        when(mock.getId()).thenReturn(uuid);
        return mock;
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Note note = new NoteAdapter(getPerson(), "A note");

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(note);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final Note note = new NoteAdapter(getPerson(), "A note");

        MockDataProvider provider = mergeProvider(assertPrimaryKey(note), assertNoReplyFields(note));

        setup(provider).persist(note);
    }

    @Test
    public void testReplyPersistsCorrectValues() throws Exception {
        final Note note = new NoteAdapter(null, "ignored").reply(getPerson(), "A note");

        MockDataProvider provider = mergeProvider(assertPrimaryKey(note), assertReplyFields(note));

        setup(provider).persist(note);
    }

    private Consumer<StatementVerifier> assertNoReplyFields(Note note) {
        return (s) -> {
            assertNullFields().accept(s);
            assertNoteFields(note, s);
        };
    }
    private Consumer<StatementVerifier> assertReplyFields(Note note) {
        return (s) -> {
            s.assertUUID(note.getReplyTo().get(), NOTE.REPLY_TO);
            assertNoteFields(note, s);
        };
    }

    private Consumer<StatementVerifier> assertNullFields() {
        return (s) -> s.assertNullFields(NOTE.REPLY_TO);
    }

    private Consumer<StatementVerifier> assertPrimaryKey(Note Note) {
        return (s) -> assertPrimaryKey(Note, s);
    }

    private Consumer<StatementVerifier> assertFields(Note Note) {
        return (s) -> assertNoteFields(Note, s);
    }

    private void assertPrimaryKey(Note note, StatementVerifier s) {
        s.assertUUID(note.getId(), NOTE.ID);
    }

    private void assertNoteFields(Note note, StatementVerifier s) {
        ZonedDateTime time = note.getCreationTime();
        s.assertSameTime(time, NOTE.LAST_UPDATED);
        s.assertSameTime(time, NOTE.CREATED);
        s.assertUUID(note.getWrittenBy(), NOTE.WRITTEN_BY);
    }

    private NoteBroker setup(MockDataProvider provider) {
        return new NoteBroker(mockConnector(provider));
    }


    @Test
    public void testFindsNoteById() throws InterruptedException {
        String id = UUID.randomUUID().toString();
        String replyId = UUID.randomUUID().toString();
        PersonManager manager = new PersonManager();
        String personId = manager.createPerson().getId();
        Clock clock = Clock.systemDefaultZone();
        ZonedDateTime createdTime = ZonedDateTime.now(clock);
        ZonedDateTime updatedTime = ZonedDateTime.now(Clock.tick(clock, Duration.ofMinutes(1)));

        MockDataProvider provider = selectOne(
                (s) -> s.assertUUID(id, NOTE.ID),
                NOTE,
                record->{
                    record.setId(id.getBytes());
                    record.setCreated(Timestamp.from(createdTime.toInstant()));
                    record.setLastUpdated(Timestamp.from(updatedTime.toInstant()));
                    record.setReplyTo(replyId.getBytes());
                    record.setWrittenBy(personId.getBytes());
                });

        Note result = setup(provider).findNote(id, manager).get();

        assertEquals(id, result.getId());
        assertEquals(replyId, result.getReplyTo().get());
        assertEquals(personId, result.getWrittenBy().getId());
        assertEquals(createdTime, result.getCreationTime());
        assertEquals(updatedTime, result.getLastUpdatedTime());
        assertTrue(result.getReplies().isEmpty());
    }


}