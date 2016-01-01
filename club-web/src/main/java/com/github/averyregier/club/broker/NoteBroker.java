package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.NoteRecord;
import com.github.averyregier.club.domain.club.Note;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Note.NOTE;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 12/28/15.
 */
public class NoteBroker extends PersistenceBroker<Note> {
    public NoteBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Note note, DSLContext create) {
        if(!equalsAny(create.insertInto(NOTE)
                .set(NOTE.ID, note.getId().getBytes())
                .set(mapFields(note))
                .onDuplicateKeyUpdate()
                .set(mapFields(note))
                .execute(), 1, 2)) {
            fail("Clubber persistence failed: " + note.getId());
        }

    }

    private Map<TableField<NoteRecord, ?>, Object> mapFields(Note note) {
        return JooqUtil.<NoteRecord>map()
                .set(NOTE.CREATED, note.getCreationTime())
                .setUUID(NOTE.REPLY_TO, note.getReplyTo())
                .set(NOTE.LAST_UPDATED, note.getLastUpdatedTime())
                .setHasUUID(NOTE.WRITTEN_BY, note.getWrittenBy())
                .build();
    }
}
