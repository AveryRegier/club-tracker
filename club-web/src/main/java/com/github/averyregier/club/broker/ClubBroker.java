package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Club;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Club.CLUB;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

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

    public Optional<Club> find(String clubId, ClubManager clubManager) {
        Optional<Club> result = query(create -> {
            ClubRecord record = create.selectFrom(CLUB)
                    .where(CLUB.ID.eq(clubId.getBytes()))
                    .fetchOne();
            if (record == null) return Optional.empty();
            return clubManager.injectClub(
                    convert(record.getId()),
                    convert(record.getParentClubId()),
                    record.getCurriculum());
        });
        return result;
    }
}
