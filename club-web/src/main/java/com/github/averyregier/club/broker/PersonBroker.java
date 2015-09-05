package com.github.averyregier.club.broker;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.db.tables.records.PersonRecord;
import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.NameBuilder;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.repository.PersistedPerson;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Person.PERSON;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
* Created by avery on 2/14/15.
*/
public class PersonBroker extends PersistenceBroker<Person> {

    private ClubFactory factory;

    public PersonBroker(ClubFactory factory) {
        super(factory.getConnector());
        this.factory = factory;
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
                .set(PERSON.GENDER, person.getGender().map(Person.Gender::getPersistenceValue))
                .set(PERSON.FRIENDLY, nameField(person, Name::getFriendlyName))
                .set(PERSON.GIVEN, nameField(person, Name::getGivenName))
                .set(PERSON.SURNAME, nameField(person, Name::getSurname))
                .set(PERSON.HONORIFIC, nameField(person, Name::getHonorificName))
                .set(PERSON.TITLE, nameField2(person, Name::getTitle))
                .set(PERSON.EMAIL, person.getEmail())
                .build();
    }

    private Optional<String> nameField(Person person, Function<Name, String> fn) {
        return Optional.ofNullable(person.getName()).map(fn);
    }
    private Optional<String> nameField2(Person person, Function<Name, Optional<String>> fn) {
        return Optional.ofNullable(person.getName()).map(fn).orElse(Optional.empty());
    }

    public Optional<Person> find(String id) {
        return query(create -> create
                .selectFrom(PERSON)
                .where(PERSON.ID.eq(id.getBytes()))
                .fetch().stream()
                .findFirst()
                .map(r -> map(r, id)));
    }

    public Collection<Person> findAll() {
        return query(create -> create
                .selectFrom(PERSON)
                .fetch().stream()
                .map((r) -> map(r, convert(r.getId())))
                .collect(Collectors.toList()));
    }

    private Person map(PersonRecord r, String id) {
        PersonAdapter person = new PersistedPerson(factory, id);
        Person.Gender.lookup(r.getGender()).ifPresent(person::setGender);
        person.setEmail(r.getEmail());
        person.setName(new NameBuilder()
                .given(r.getGiven())
                .surname(r.getSurname())
                .title(r.getTitle())
                .friendly(r.getFriendly())
                .honorific(r.getHonorific())
                .build());
        return person;
    }
}
