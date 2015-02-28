package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.LeaderRecord;
import com.github.averyregier.club.domain.club.ClubLeader;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Leader.LEADER;

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
            fail("Person persistence failed: " + leader.getId());
        }
    }

    private Map<TableField<LeaderRecord, ?>, Object> mapFields(ClubLeader leader) {
        return JooqUtil.<LeaderRecord>map()
                .set(LEADER.ROLE, leader.getLeadershipRole().name())
                .set(LEADER.CLUB_ID, leader.getClub().map(club -> club.getId().getBytes()))
                .build();
    }
}
