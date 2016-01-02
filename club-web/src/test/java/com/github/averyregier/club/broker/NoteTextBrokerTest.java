package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Note;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.NoteAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.NoteText.NOTE_TEXT;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by avery on 1/1/16.
 */
public class NoteTextBrokerTest {
    private Integer sequence = -1;

    @Test
    public void testPersistMerges() throws Exception {
        final Note note = new NoteAdapter(getPerson(), "A note");

        MockDataProvider provider = batch()
                .statement(mergeProvider(assertPrimaryKey(note), assertFields(note)))
                .statement(deleteExtraParts(note)).build();

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

        MockDataProvider provider = batch()
                .statement(insertPart(note))
                .statement(deleteExtraParts(note))
                .build();

        setup(provider).persist(note);
    }

    @Test
    public void testBigNote() throws Exception {
        final Note note = new NoteAdapter(getPerson(), thousandCharacters()+thousandCharacters());

        MockDataProvider provider = batch()
                .statement(insertPart(note))
                .statement(insertPart(note))
                .statement(insertPart(note))
                .statement(deleteExtraParts(note))
                .build();

        setup(provider).persist(note);
    }

    private MockDataProvider insertPart(Note note) {
        return mergeProvider(assertPrimaryKey(note), (s) -> assertNoteFields(note, s));
    }

    private MockDataProvider deleteExtraParts(Note note) {
        return delete(assertPrimaryKey(note), 0);
    }

    private String thousandCharacters() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<1000; i++) {
            sb.append((char)(i%100));
        }
        return sb.toString();
    }


    private Consumer<StatementVerifier> assertPrimaryKey(Note Note) {
        return (s) -> assertPrimaryKey(Note, s);
    }

    private Consumer<StatementVerifier> assertFields(Note Note) {
        return (s) -> assertNoteFields(Note, s);
    }

    private void assertPrimaryKey(Note note, StatementVerifier s) {
        s.assertUUID(note.getId(), NOTE_TEXT.ID);
        switch (s.getType()) {
            case MERGE:
            case DELETE:
                s.assertFieldEquals(sequence + 1, NOTE_TEXT.SEQUENCE);
                break;
            case INSERT:
                s.assertFieldEquals(sequence, NOTE_TEXT.SEQUENCE);
                break;
            default:
                fail(s.getType()+" not expected");
        }
        sequence = s.get(NOTE_TEXT.SEQUENCE);
    }

    private void assertNoteFields(Note note, StatementVerifier s) {
        assertTrue(s.get(NOTE_TEXT.NOTE).length() <= 1000);

        String text = note.getText();
        String part = text.substring(sequence * 1000, Math.min((sequence + 1) * 1000, text.length()));
        s.assertFieldEquals(part, NOTE_TEXT.NOTE);
    }

    private NoteTextBroker setup(MockDataProvider provider) {
        return new NoteTextBroker(mockConnector(provider));
    }



}