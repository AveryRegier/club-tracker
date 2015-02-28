package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubRecord;
import com.github.averyregier.club.domain.club.Club;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Club.CLUB;

/**
 * Created by avery on 2/25/15.
 */
public class ClubBroker extends Broker<Club> {
    public ClubBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Club club, DSLContext create) {
        if(create.insertInto(CLUB)
                .set(CLUB.ID, club.getId().getBytes())
                .set(mapFields(club))
                .onDuplicateKeyUpdate()
                .set(mapFields(club))
                .execute() != 1) {
            fail("Club persistence failed: " + club.getId());
        }

    }

    private Map<TableField<ClubRecord, ?>, Object> mapFields(Club club) {
        return JooqUtil.<ClubRecord>map()
                .set(CLUB.PARENT_CLUB_ID, club.getParentGroup().map(g -> g.getId().getBytes()).orElse(null))
                .set(CLUB.CURRICULUM, club.getCurriculum().getId())
                .build();
    }
}
