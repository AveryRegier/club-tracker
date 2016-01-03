package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.NoteRecord;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Note;
import com.github.averyregier.club.domain.club.adapter.NoteAdapter;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Note.NOTE;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
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
            fail("Note metadata persistence failed: " + note.getId());
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

    public Optional<Note> findNote(String id, PersonManager manager) {
        return query((create)-> create
                .selectFrom(NOTE)
                .where(NOTE.ID.eq(id.getBytes()))
                .fetch().stream().findFirst().map(r->
                    new NoteAdapter(
                        convert(r.getId()),
                        manager.lookup(convert(r.getWrittenBy())).get(),
                        new NoteTextBroker(connector).findNoteText(id).get(),
                        r.getCreated().toInstant().atZone(ZoneId.systemDefault()),
                        r.getLastUpdated().toInstant().atZone(ZoneId.systemDefault()),
                        convert(r.getReplyTo())
                    )));
    }
}
