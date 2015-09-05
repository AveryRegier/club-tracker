package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ParentRecord;
import com.github.averyregier.club.domain.club.Parent;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Parent.PARENT;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/28/15.
 */
public class ParentBroker extends PersistenceBroker<Parent> {
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
                .setHasId(PARENT.FAMILY_ID, thing.getFamily())
                .build();
    }

    public Optional<String> findFamily(String personId) {
        return query((create)-> create
                .selectFrom(PARENT)
                .where(PARENT.ID.eq(personId.getBytes()))
                .fetch().stream()
                .findFirst()
                .map(r -> convert(r.getFamilyId())));
    }

}
