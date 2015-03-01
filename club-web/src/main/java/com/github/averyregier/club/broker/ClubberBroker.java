package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubberRecord;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.program.AgeGroup;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Clubber.CLUBBER;

/**
 * Created by avery on 2/28/15.
 */
public class ClubberBroker extends Broker<Clubber> {
    public ClubberBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Clubber clubber, DSLContext create) {
        if(create.insertInto(CLUBBER)
                .set(CLUBBER.ID, clubber.getId().getBytes())
                .set(mapFields(clubber))
                .onDuplicateKeyUpdate()
                .set(mapFields(clubber))
                .execute() != 1) {
            fail("Clubber persistence failed: " + clubber.getId());
        }
    }

    private Map<TableField<ClubberRecord, ?>, Object> mapFields(Clubber clubber) {
        return JooqUtil.<ClubberRecord>map()
                .set(CLUBBER.CLUB_ID, clubber.getClub().map(club -> club.getId().getBytes()))
                .set(CLUBBER.FAMILY_ID, clubber.getFamily().map(family -> family.getId().getBytes()))
                .set(CLUBBER.AGE_GROUP, Optional.ofNullable(clubber.getCurrentAgeGroup()).map(AgeGroup::name))
                .build();
    }
}
