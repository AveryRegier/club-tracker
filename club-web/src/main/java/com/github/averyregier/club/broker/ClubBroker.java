package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Club;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
                .setHasId(CLUB.PARENT_CLUB_ID, club.getParentGroup())
                .set(CLUB.CURRICULUM, club.getCurriculum().getId())
                .build();
    }

    public Optional<Club> find(String clubId, ClubManager clubManager) {
        Function<DSLContext, Optional<Club>> fn = create -> {
            ClubRecord record = create.selectFrom(CLUB)
                    .where(CLUB.ID.eq(clubId.getBytes()))
                    .fetchOne();
            if (record == null) return Optional.empty();
            return clubManager.constructClub(
                    convert(record.getId()),
                    convert(record.getParentClubId()),
                    record.getCurriculum());
        };
        Optional<Club> result = query(fn);
        return result;
    }

    public Collection<Club> findChildren(Club parentClub, ClubManager clubManager) {
        return query((Function<DSLContext, Collection<Club>>) create -> create
                .selectFrom(CLUB)
                .where(CLUB.PARENT_CLUB_ID.eq(parentClub.getId().getBytes()))
                .fetch()
                .map(record -> clubManager.constructClub(
                        convert(record.getId()),
                        parentClub,
                        record.getCurriculum()).get()));
    }
}
