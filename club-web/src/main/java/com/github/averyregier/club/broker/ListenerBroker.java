package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ListenerRecord;
import com.github.averyregier.club.domain.club.Listener;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Listener.LISTENER;

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
                .set(LISTENER.CLUB_ID, listener.getClub().map(club -> club.getId().getBytes()))
                .build();
    }
}
