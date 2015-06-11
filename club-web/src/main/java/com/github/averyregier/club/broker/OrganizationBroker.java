package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.OrganizationRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Organization.ORGANIZATION;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/27/15.
 */
public class OrganizationBroker extends Broker<Program> {
    public OrganizationBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Program program, DSLContext create) {
        if(create.insertInto(ORGANIZATION)
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
                .set(ORGANIZATION.CLUB_ID, program.getId().getBytes())
                .set(ORGANIZATION.ORGANIZATIONNAME, program.getShortCode())
                .set(ORGANIZATION.LOCALE, Optional.ofNullable(program.getLocale()).map(Object::toString).orElse(null))
                .build();
    }

    public Optional<Program> find(String id, ClubManager manager) {
        return query(create-> {
            Result<OrganizationRecord> records = create.selectFrom(ORGANIZATION)
                    .where(ORGANIZATION.ID.eq(id.getBytes()))
                    .fetch();
            return records.stream().findFirst().map(r->new ProgramAdapter(
                    r.getLocale(),
                    r.getOrganizationname(),
                    manager.lookup(convert(r.getClubId()))) {
                @Override
                public String getId() {
                    return id;
                }
            });
        });
    }
}
