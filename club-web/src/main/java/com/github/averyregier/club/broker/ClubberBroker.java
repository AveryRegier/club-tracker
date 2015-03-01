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
    protected void persist(Clubber thing, DSLContext create) {
        if(create.insertInto(CLUBBER)
                .set(CLUBBER.ID, thing.getId().getBytes())
                .set(mapFields(thing))
                .onDuplicateKeyUpdate()
                .set(mapFields(thing))
                .execute() != 1) {
            fail("Clubber persistence failed: " + thing.getId());
        }
    }

    private Map<TableField<ClubberRecord, ?>, Object> mapFields(Clubber thing) {
        return JooqUtil.<ClubberRecord>map()
                .set(CLUBBER.CLUB_ID, thing.getClub().map(club->club.getId().getBytes()))
                .set(CLUBBER.FAMILY_ID, thing.getFamily().map(family -> family.getId().getBytes()))
                .set(CLUBBER.AGE_GROUP, Optional.ofNullable(thing.getCurrentAgeGroup()).map(AgeGroup::name))
                .build();
    }
}
