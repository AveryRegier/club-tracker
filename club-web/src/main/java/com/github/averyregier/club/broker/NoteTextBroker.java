package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Note;
import org.jooq.DSLContext;

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
    }
}
