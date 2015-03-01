package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Family;
import org.jooq.DSLContext;

import static com.github.averyregier.club.db.tables.Family.FAMILY;

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
}
