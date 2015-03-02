package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.RecordRecord;
import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.club.Signing;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Record.RECORD;

/**
 * Created by avery on 3/1/15.
 */
public class ClubberRecordBroker extends Broker<ClubberRecord> {
    public ClubberRecordBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(ClubberRecord record, DSLContext create) {
        if(create.insertInto(RECORD)
                .set(RECORD.CLUBBER_ID, record.getClubber().getId().getBytes())
                .set(RECORD.SECTION_ID, record.getSection().getId())
                .set(mapFields(record))
                .onDuplicateKeyUpdate()
                .set(mapFields(record))
                .execute() != 1) {
            fail("Clubber persistence failed: " + record.getClubber().getId() + " for " + record.getSection().getId());
        }
    }

    private Map<TableField<RecordRecord, ?>, Object> mapFields(ClubberRecord record) {
        JooqUtil.MapBuilder<RecordRecord> map = JooqUtil.<RecordRecord>map();
        if(record.getSigning().isPresent()) {
            Signing signing = record.getSigning().get();
            map .set(RECORD.CLUB_ID, signing.by().getClub().map(club -> club.getId().getBytes()))
                .set(RECORD.SIGNED_BY, signing.by().getId().getBytes())
                .set(RECORD.SIGN_DATE, new java.sql.Date(signing.getDate().toEpochDay()))
                .set(RECORD.NOTE, signing.getNote());
        } else {
            map.setNull(RECORD.CLUB_ID, RECORD.SIGNED_BY, RECORD.SIGN_DATE, RECORD.NOTE);
        }
        return map.build();
    }
}
