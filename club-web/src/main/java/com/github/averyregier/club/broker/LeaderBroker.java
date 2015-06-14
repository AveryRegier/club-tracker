package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.LeaderRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ClubLeaderAdapter;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.averyregier.club.db.tables.Leader.LEADER;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/28/15.
 */
public class LeaderBroker extends Broker<ClubLeader> {
    public LeaderBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(ClubLeader leader, DSLContext create) {
        if(create.insertInto(LEADER)
                .set(LEADER.ID, leader.getId().getBytes())
                .set(mapFields(leader))
                .onDuplicateKeyUpdate()
                .set(mapFields(leader))
                .execute() != 1) {
            fail("Leader persistence failed: " + leader.getId());
        }
    }

    private Map<TableField<LeaderRecord, ?>, Object> mapFields(ClubLeader leader) {
        return JooqUtil.<LeaderRecord>map()
                .set(LEADER.ROLE, leader.getLeadershipRole().name())
                .setHasId(LEADER.CLUB_ID, leader.getClub())
                .build();
    }

    public Optional<ClubLeader> find(String id, PersonManager personManager, ClubManager clubManager) {
        Function<DSLContext, Optional<ClubLeader>> fn = create -> {
            LeaderRecord record = create.selectFrom(LEADER).where(LEADER.ID.eq(id.getBytes())).fetchOne();
            if (record == null) return Optional.empty();

            return Optional.of(new ClubLeaderAdapter(personManager.lookup(id).get(),
                    ClubLeader.LeadershipRole.valueOf(record.getRole()),
                    (ClubAdapter) clubManager.lookup(convert(record.getClubId())).get()
            ));
        };
        Optional<ClubLeader> result = query(fn);
        return result;
    }
}
