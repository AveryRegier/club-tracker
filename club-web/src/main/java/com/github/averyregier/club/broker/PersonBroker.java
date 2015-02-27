package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.PersonRecord;
import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.averyregier.club.db.tables.Person.PERSON;

/**
* Created by avery on 2/14/15.
*/
public class PersonBroker extends Broker<Person> {

    public PersonBroker(Connector connector) {
        super(connector);
    }

    protected void persist(Person person, DSLContext create) {
        if(create.insertInto(PERSON)
                .set(PERSON.ID, person.getId().getBytes())
                .set(mapFields(person))
                .onDuplicateKeyUpdate()
                .set(mapFields(person))
                .execute() != 1) {
            fail("Person persistence failed: " + person.getId());
        }
    }

    private Map<TableField<PersonRecord, ?>, Object> mapFields(Person person) {
        return JooqUtil.<PersonRecord>map()
                .set(PERSON.GENDER, person.getGender().map(Enum::name))
                .set(PERSON.FRIENDLY, nameField(person, Name::getFriendlyName))
                .set(PERSON.GIVEN, nameField(person, Name::getGivenName))
                .set(PERSON.SURNAME, nameField(person, Name::getSurname))
                .set(PERSON.HONORIFIC, nameField(person, Name::getHonorificName))
                .set(PERSON.TITLE, nameField(person, n -> n.getTitle().orElse(null)))
                .set(PERSON.EMAIL, person.getEmail())
                .build();
    }

    private Optional<String> nameField(Person person, Function<Name, String> fn) {
        return Optional.ofNullable(person.getName()).map(fn);
    }
}
