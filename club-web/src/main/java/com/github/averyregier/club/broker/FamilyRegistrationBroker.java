package com.github.averyregier.club.broker;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.db.tables.records.FamilyRegistrationRecord;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.utility.InputField;
import org.jooq.DSLContext;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.FamilyRegistration.FAMILY_REGISTRATION;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 8/27/15.
 */
public class FamilyRegistrationBroker extends PersistenceBroker<Family> {
    private ClubFactory factory;

    public FamilyRegistrationBroker(ClubFactory factory) {
        super(factory.getConnector());
        this.factory = factory;
    }

    @Override
    protected void persist(Family family, DSLContext create) {
        deleteOldRecords(family, create);
        insertAllValues(family, create);
    }

    private void insertAllValues(Family family, DSLContext create) {
        family.getValues().entrySet().stream().forEach(e -> {
            create.insertInto(FAMILY_REGISTRATION)
                    .set(FAMILY_REGISTRATION.FAMILY_ID, family.getId().getBytes())
                    .set(FAMILY_REGISTRATION.INPUT_FIELD_ID, e.getKey().getShortCode().getBytes())
                    .set(FAMILY_REGISTRATION.THE_VALUE, e.getValue())
                    .onDuplicateKeyUpdate()
                    .set(FAMILY_REGISTRATION.THE_VALUE, e.getValue())
                    .execute();
        });
    }

    private void deleteOldRecords(Family family, DSLContext create) {
        create.delete(FAMILY_REGISTRATION)
                .where(FAMILY_REGISTRATION.FAMILY_ID.eq(family.getId().getBytes()))
                .execute();
    }

    public Map<InputField, String> getRegistration(String familyId) {
        return query(create -> create.selectFrom(FAMILY_REGISTRATION)
            .where(FAMILY_REGISTRATION.FAMILY_ID.eq(familyId.getBytes()))
            .fetch().stream()
            .collect(Collectors.toMap(
                    getKeyFn(),
                    FamilyRegistrationRecord::getTheValue)));
    }

    private Function<FamilyRegistrationRecord, InputField> getKeyFn() {
        return r->factory
                .getClubManager()
                .getRegistrationField(convert(r.getInputFieldId()))
                .orElseThrow(IllegalArgumentException::new);
    }
}
