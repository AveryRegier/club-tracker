package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.RegistrationFormRecord;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.RegistrationSection;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.RegistrationForm.REGISTRATION_FORM;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 8/23/15.
 */
public class RegistrationFormBroker extends Broker {
    public RegistrationFormBroker(Connector connector) {
        super(connector);
    }

    public void persist(Program program, InputFieldGroup group) {
        execute(create -> {
            if (!equalsAny(create.insertInto(REGISTRATION_FORM)
                    .set(REGISTRATION_FORM.ORGANIZATION_ID, program.getId().getBytes())
                    .set(REGISTRATION_FORM.TYPE, group.getName())
                    .set(REGISTRATION_FORM.INPUT_GROUP_ID, group.getId().getBytes())
                    .onDuplicateKeyUpdate()
                    .set(REGISTRATION_FORM.INPUT_GROUP_ID, group.getId().getBytes())
                    .execute(), 1, 2)) {
                fail("Failed to persist " + group.getShortCode());
            }
        });
    }

    public Map<RegistrationSection, InputFieldGroup> find(String programId, Locale locale) {
        return query(create -> find(programId, locale, create));
    }

    protected Map<RegistrationSection, InputFieldGroup> find(String programId, Locale locale, DSLContext create) {
        Result<RegistrationFormRecord> records = create.selectFrom(REGISTRATION_FORM)
                .where(REGISTRATION_FORM.ORGANIZATION_ID.eq(programId.getBytes()))
                .fetch();
        return records.stream()
                .collect(Collectors.toMap(
                        r -> RegistrationSection.valueOf(r.getType()),
                        r -> map(locale, convert(r.getInputGroupId()), r.getType())));
    }

    private InputFieldGroup map(Locale locale, String id, String type) {
        return new InputFieldGroupBroker(connector)
                .find(id, locale)
                .get()
                .name(type)
                .build();
    }
}
