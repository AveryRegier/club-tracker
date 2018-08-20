package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubYearRecord;
import com.github.averyregier.club.domain.club.ClubYear;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.ClubYear.CLUB_YEAR;

public class ClubYearBroker extends PersistenceBroker<ClubYear> {
    public ClubYearBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(ClubYear thing, DSLContext create) {
        if (create.insertInto(CLUB_YEAR)
                .set(CLUB_YEAR.ID, thing.getId().getBytes())
                .set(mapFields(thing))
                .onDuplicateKeyUpdate()
                .set(mapFields(thing))
                .execute() != 1) {
            fail("Club Year persistence failed: " + thing.getId());
        }
    }

    private Map<TableField<ClubYearRecord, ?>, Object> mapFields(ClubYear thing) {
        return JooqUtil.<ClubYearRecord>map()
                .set(CLUB_YEAR.CLUB_ID, thing.getClub())
                .set(CLUB_YEAR.NAME, thing.getClubYear())
                .build();
    }
}
