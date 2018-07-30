package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.LeaderRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ClubLeaderAdapter;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.averyregier.club.db.tables.Leader.LEADER;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 2/28/15.
 */
public class LeaderBroker extends PersistenceBroker<ClubLeader> {
    public LeaderBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(ClubLeader leader, DSLContext create) {
        if(!equalsAny(create.insertInto(LEADER)
                .set(LEADER.ID, leader.getId().getBytes())
                .set(LEADER.CLUB_ID, leader.getClub().map(c->c.getId().getBytes()).orElse(null))
                .set(mapFields(leader))
                .onDuplicateKeyUpdate()
                .set(mapFields(leader))
                .execute(), 1, 2)) {
            fail("Leader persistence failed: " + leader.getId());
        }
    }

    private Map<TableField<LeaderRecord, ?>, Object> mapFields(ClubLeader leader) {
        return JooqUtil.<LeaderRecord>map()
                .set(LEADER.ROLE, leader.getLeadershipRole().name())
                .build();
    }

    public Optional<ClubLeader> find(String id, PersonManager personManager, ClubManager clubManager) {
        Function<DSLContext, Optional<ClubLeader>> fn = create -> {
            Result<LeaderRecord> results = create.selectFrom(LEADER).where(LEADER.ID.eq(id.getBytes())).fetch();

            if (results == null) return Optional.empty();
            return results.stream().map(record -> (ClubLeader) new ClubLeaderAdapter(personManager.lookup(id).get(),
                    ClubLeader.LeadershipRole.valueOf(record.getRole()),
                    (ClubAdapter) clubManager.lookup(convert(record.getClubId())).get()
            )).min(Comparator.reverseOrder());
        };
        return query(fn);
    }
}
