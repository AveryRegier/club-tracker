package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.NoteTextRecord;
import com.github.averyregier.club.domain.club.Note;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.NoteText.NOTE_TEXT;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 12/28/15.
 */
public class NoteTextBroker extends PersistenceBroker<Note> {
    public NoteTextBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Note note, DSLContext create) {
        String text = note.getText();
        int numParts = (text.length() / 1000) + 1;
        for(int i=0; i<numParts; i++) {
            String part = text.substring((i*1000), Math.min(1000*(i+1), text.length()));
            if(!equalsAny(create.insertInto(NOTE_TEXT)
                    .set(NOTE_TEXT.ID, note.getId().getBytes())
                    .set(NOTE_TEXT.SEQUENCE, i)
                    .set(NOTE_TEXT.NOTE, part)
                    .onDuplicateKeyUpdate()
                    .set(NOTE_TEXT.NOTE, part)
                    .execute(), 1, 2)) {
                fail("Note text persistence failed: " + note.getId());
            }
        }
        create.delete(NOTE_TEXT)
                .where(NOTE_TEXT.ID.eq(note.getId().getBytes()))
                .and(NOTE_TEXT.SEQUENCE.ge(numParts))
                .execute();
    }

    public Optional<String> findNoteText(String id) {
        return query((create) -> {
            Result<NoteTextRecord> result = create
                    .selectFrom(NOTE_TEXT)
                    .where(NOTE_TEXT.ID.eq(id.getBytes()))
                    .orderBy(NOTE_TEXT.SEQUENCE)
                    .fetch();
            Optional<String> note = result.isEmpty() ? Optional.empty() :
                    Optional.of(result.stream()
                        .map(NoteTextRecord::getNote)
                        .collect(Collectors.joining()));
            return note;
        });
    }
}
