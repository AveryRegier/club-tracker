package com.github.averyregier.club.broker;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.db.tables.records.PersonRegistrationRecord;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputField;
import org.jooq.DSLContext;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.PersonRegistration.PERSON_REGISTRATION;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 8/27/15.
 */
public class PersonRegistrationBroker extends PersistenceBroker<Person> {
    private ClubFactory factory;

    public PersonRegistrationBroker(ClubFactory factory) {
        super(factory.getConnector());
        this.factory = factory;
    }

    @Override
    protected void persist(Person person, DSLContext create) {
        deleteOldRecords(person, create);
        insertAllValues(person, create);
    }

    private void insertAllValues(Person person, DSLContext create) {
        person.getValues().entrySet().stream().forEach(e -> {
            create.insertInto(PERSON_REGISTRATION)
                    .set(PERSON_REGISTRATION.PERSON_ID, person.getId().getBytes())
                    .set(PERSON_REGISTRATION.INPUT_FIELD_ID, e.getKey().getShortCode().getBytes())
                    .set(PERSON_REGISTRATION.THE_VALUE, e.getValue())
                    .onDuplicateKeyUpdate()
                    .set(PERSON_REGISTRATION.THE_VALUE, e.getValue())
                    .execute();
        });
    }

    private void deleteOldRecords(Person person, DSLContext create) {
        create.delete(PERSON_REGISTRATION)
                .where(PERSON_REGISTRATION.PERSON_ID.eq(person.getId().getBytes()))
                .execute();
    }

    public Map<InputField, String> find(String id) {
        return getRegistration(id);
    }

    public Map<InputField, String> getRegistration(String familyId) {
        return query(create -> create.selectFrom(PERSON_REGISTRATION)
                .where(PERSON_REGISTRATION.PERSON_ID.eq(familyId.getBytes()))
                .fetch().stream()
                .collect(Collectors.toMap(
                        getKeyFn(),
                        PersonRegistrationRecord::getTheValue)));
    }

    private Function<PersonRegistrationRecord, InputField> getKeyFn() {
        return r->factory
                .getClubManager()
                .getRegistrationField(convert(r.getInputFieldId()))
                .orElseThrow(IllegalArgumentException::new);
    }

}
