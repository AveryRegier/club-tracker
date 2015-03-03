package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.CeremonyRecord;
import com.github.averyregier.club.domain.club.Ceremony;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Ceremony.CEREMONY;

/**
 * Created by avery on 3/2/15.
 */
public class CeremonyBroker extends Broker<Ceremony> {
    public CeremonyBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Ceremony ceremony, DSLContext create) {
        if(create.insertInto(CEREMONY)
                .set(CEREMONY.ID, ceremony.getId().getBytes())
                .set(mapFields(ceremony))
                .onDuplicateKeyUpdate()
                .set(mapFields(ceremony))
                .execute() != 1) {
            fail("Ceremony persistence failed: " + ceremony.getId());
        }

    }

    private Map<TableField<CeremonyRecord, ?>, Object> mapFields(Ceremony ceremony) {
        return JooqUtil.<CeremonyRecord>map()
                .set(CEREMONY.PRESENTATION_DATE, ceremony.presentationDate() != null ?
                        new java.sql.Date(ceremony.presentationDate().toEpochDay()):
                        null)
                .set(CEREMONY.NAME, ceremony.getName())
                .build();
    }
}
