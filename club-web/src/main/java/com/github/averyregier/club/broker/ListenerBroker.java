package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ListenerRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ListenerAdapter;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.averyregier.club.db.tables.Listener.LISTENER;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/28/15.
 */
public class ListenerBroker extends Broker<Listener> {
    public ListenerBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Listener listener, DSLContext create) {
        if(create.insertInto(LISTENER)
                .set(LISTENER.ID, listener.getId().getBytes())
                .set(mapFields(listener))
                .onDuplicateKeyUpdate()
                .set(mapFields(listener))
                .execute() != 1) {
            fail("Listener persistence failed: " + listener.getId());
        }
    }

    private Map<TableField<ListenerRecord, ?>, Object> mapFields(Listener listener) {
        return JooqUtil.<ListenerRecord>map()
                .setHasId(LISTENER.CLUB_ID, listener.getClub())
                .build();
    }

    public Optional<Listener> find(String id, PersonManager personManager, ClubManager clubManager) {
        Function<DSLContext, Optional<Listener>> fn = create -> {
            ListenerRecord record = create.selectFrom(LISTENER).where(LISTENER.ID.eq(id.getBytes())).fetchOne();
            if (record == null) return Optional.empty();

            ClubAdapter clubAdapter = (ClubAdapter) clubManager.lookup(convert(record.getClubId())).get();
            ListenerAdapter listener = new ListenerAdapter(personManager.lookup(id).get());
            listener.setClubGroup(clubAdapter);
            return Optional.of(listener);
        };
        Optional<Listener> result = query(fn);
        return result;
    }
}
