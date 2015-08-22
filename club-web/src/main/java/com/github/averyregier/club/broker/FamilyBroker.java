package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.Clubber;
import com.github.averyregier.club.db.tables.Parent;
import com.github.averyregier.club.db.tables.records.FamilyRecord;
import com.github.averyregier.club.domain.club.Family;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.stream.Stream;

import static com.github.averyregier.club.db.tables.Family.FAMILY;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/28/15.
 */
public class FamilyBroker extends Broker<Family> {
    public FamilyBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Family family, DSLContext create) {
        if(create.insertInto(FAMILY)
                .set(FAMILY.ID, family.getId().getBytes())
                .set(mapFields(family))
                .onDuplicateKeyUpdate()
                .set(mapFields(family))
                .execute() != 1) {
            fail("Family persistence failed: " + family.getId());
        }
    }

    private Map<TableField<FamilyRecord, ?>, Object> mapFields(Family family) {
        return JooqUtil.<FamilyRecord>map()
                .set(FAMILY.ADDRESS_ID, family.getAddress().map(a->a.getId().getBytes()).orElse(null))
                .build();
    }

    public Stream<String> getAllFamilyMembers(String familyId) {
        return query(create -> create
                .select(Parent.PARENT.ID)
                .from(Parent.PARENT)
                .where(Parent.PARENT.FAMILY_ID.eq(familyId.getBytes()))
                .union(create.select(Clubber.CLUBBER.ID)
                        .from(Clubber.CLUBBER)
                        .where(Clubber.CLUBBER.FAMILY_ID.eq(familyId.getBytes()))).fetch()
                .stream().map(r -> convert(r.value1())));
    }
}
