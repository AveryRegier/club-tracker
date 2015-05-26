package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.Clubber;
import com.github.averyregier.club.db.tables.Parent;
import com.github.averyregier.club.domain.club.Family;
import org.jooq.Condition;
import org.jooq.DSLContext;

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
                .onDuplicateKeyIgnore()
                .execute() != 1) {
            fail("Family persistence failed: " + family.getId());
        }

    }

    public Stream<String> getAllFamilyMembers(String familyId) {
        return query(create -> {
            Condition isPartOfFamily = Parent.PARENT.FAMILY_ID.eq(familyId.getBytes());
            return create.select(Parent.PARENT.ID).where(isPartOfFamily)
                    .union(create.select(Clubber.CLUBBER.ID).where(isPartOfFamily)).fetch()
                    .stream().map(r -> convert(r.value1()));
        });
    }
}
