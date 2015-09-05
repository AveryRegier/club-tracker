package com.github.averyregier.club.broker;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.db.tables.Clubber;
import com.github.averyregier.club.db.tables.Parent;
import com.github.averyregier.club.db.tables.records.FamilyRecord;
import com.github.averyregier.club.domain.club.Address;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.repository.PersistedFamily;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.averyregier.club.db.tables.Family.FAMILY;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/28/15.
 */
public class FamilyBroker extends PersistenceBroker<Family> {
    private ClubFactory factory;

    public FamilyBroker(ClubFactory factory) {
        super(factory.getConnector());
        this.factory = factory;
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

    public PersistedFamily getPersistedFamily(String familyId, List<Person> members) {
        PersistedFamily family = new PersistedFamily(familyId, members);
        members.forEach(m -> m.getUpdater().setFamily(family));
        getAddress(familyId).ifPresent(family::setAddress);
        family.setValues(new FamilyRegistrationBroker(factory).getRegistration(familyId));
        return family;
    }

    private Optional<Address> getAddress(String familyId) {
        return new AddressBroker(connector).find(getAddressId(familyId));
    }

    protected String getAddressId(String familyId) {
        return query(create -> convert(create
                .selectFrom(FAMILY)
                .where(FAMILY.ID.eq(familyId.getBytes()))
                .fetchOne().getAddressId()));
    }
}
