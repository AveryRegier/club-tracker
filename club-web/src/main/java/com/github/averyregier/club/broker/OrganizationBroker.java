package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.OrganizationRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.RegistrationSection;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Club.CLUB;
import static com.github.averyregier.club.db.tables.Organization.ORGANIZATION;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static com.github.averyregier.club.domain.utility.UtilityMethods.parseLocale;

/**
 * Created by avery on 2/27/15.
 */
public class OrganizationBroker extends PersistenceBroker<Program> {
    public OrganizationBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Program program, DSLContext create) {
        if (create.insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, program.getId().getBytes())
                .set(mapFields(program))
                .onDuplicateKeyUpdate()
                .set(mapFields(program))
                .execute() != 1) {
            fail("Organization persistence failed: " + program.getId());
        }
    }

    private Map<TableField<OrganizationRecord, ?>, Object> mapFields(Program program) {
        return JooqUtil.<OrganizationRecord>map()
                .set(ORGANIZATION.CLUB_ID, program)
                .set(ORGANIZATION.ORGANIZATIONNAME, program.getShortCode())
                .set(ORGANIZATION.LOCALE, Optional.ofNullable(program.getLocale()).map(Object::toString))
                .build();
    }

    public Optional<Program> find(String id, ClubManager clubManager) {
        return query(create -> create
                .selectFrom(ORGANIZATION.join(CLUB)
                        .on(ORGANIZATION.CLUB_ID.eq(CLUB.ID)))
                .where(ORGANIZATION.ID.eq(id.getBytes()))
                .fetch().stream().findFirst()
                .map(r -> mapProgram(id, clubManager, r)));
    }

    private Program mapProgram(String id, ClubManager clubManager, Record r) {
        String locale = r.getValue(ORGANIZATION.LOCALE);
        return clubManager.loadProgram(
                locale,
                r.getValue(ORGANIZATION.ORGANIZATIONNAME),
                Programs.find(r.getValue(CLUB.CURRICULUM)).orElseThrow(IllegalArgumentException::new),
                id,
                () -> getRegistrationMap(id, locale));
    }

    private Map<RegistrationSection, InputFieldGroup> getRegistrationMap(String id, String locale) {
        return new RegistrationFormBroker(connector).find(id, parseLocale(locale));
    }

    public void load(ClubManager clubManager) {
        query(create -> create
                .selectFrom(ORGANIZATION.join(CLUB)
                        .on(ORGANIZATION.CLUB_ID.eq(CLUB.ID)))
                .fetch().stream().findFirst()
                .map(r -> mapProgram(convert(r.getValue(ORGANIZATION.ID)), clubManager, r)));
    }
}
