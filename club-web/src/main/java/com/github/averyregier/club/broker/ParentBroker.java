package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ParentRecord;
import com.github.averyregier.club.domain.club.Parent;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Parent.PARENT;

/**
 * Created by avery on 2/28/15.
 */
public class ParentBroker extends Broker<Parent> {
    public ParentBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Parent thing, DSLContext create) {
        if(create.insertInto(PARENT)
                .set(PARENT.ID, thing.getId().getBytes())
                .set(mapFields(thing))
                .onDuplicateKeyUpdate()
                .set(mapFields(thing))
                .execute() != 1) {
            fail("Clubber persistence failed: " + thing.getId());
        }
    }

    private Map<TableField<ParentRecord, ?>, Object> mapFields(Parent thing) {
        return JooqUtil.<ParentRecord>map()
                .set(PARENT.FAMILY_ID, thing.getFamily().map(family -> family.getId().getBytes()))
                .build();
    }
}
