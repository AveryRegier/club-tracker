package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.InputFieldRecord;
import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.adapter.InputFieldBuilder;
import com.github.averyregier.club.domain.utility.adapter.StandardInputFields;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.averyregier.club.db.tables.InputField.INPUT_FIELD;
import static com.github.averyregier.club.domain.utility.UtilityMethods.applyOrNull;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 8/17/15.
 */
public class InputFieldBroker extends PersistenceBroker<InputField> {
    public InputFieldBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(InputField field, DSLContext create) {
        try {
            if (create.insertInto(INPUT_FIELD)
                    .set(INPUT_FIELD.ID, field.getShortCode().getBytes())
                    .set(mapFields(field))
                    .onDuplicateKeyUpdate()
                    .set(mapFields(field))
                    .execute() != 1) {
                fail("Input Field persistence failed: " + field.getId());
            } else {
                try {
                    UUID.fromString(field.getShortCode());
                    InputFieldValueBroker valueBroker = new InputFieldValueBroker(connector);
                    field.getValues().ifPresent(l -> l.forEach(v -> valueBroker.persistValue(field, v, create)));
                } catch (IllegalArgumentException e) {
                    // not a UUID, so its a standard field, and we don't want to persist values
                }
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(field.getName()+" "+field.getId(), e);
        }
    }

    private Map<TableField<InputFieldRecord, ?>, Object> mapFields(InputField field) {
        Integer order = applyOrNull(field.getContainer(), c -> c.getFieldDesignations().indexOf(field) + 1);
        return JooqUtil.<InputFieldRecord>map()
                .set(INPUT_FIELD.PARENT_INPUT_GROUP_ID, applyOrNull(field.getContainer(), HasId::getShortCode))
                .set(INPUT_FIELD.NAME, field.getName())
                .set(INPUT_FIELD.TYPE, field.getType().name())
                .set(INPUT_FIELD.REQUIRED, field.isRequired() ? "T" : "F")
                .set(INPUT_FIELD.THE_ORDER, order)
                .build();
    }

    Stream<InputFieldRecord> recordsByGroup(String groupId) {
        Result<InputFieldRecord> records = query(create -> create
                .selectFrom(INPUT_FIELD)
                .where(INPUT_FIELD.PARENT_INPUT_GROUP_ID.eq(groupId.getBytes()))
                .orderBy(INPUT_FIELD.THE_ORDER)
                .fetch());
        return records.stream();
    }

    static InputFieldBuilder mapBuild(InputFieldRecord record, Connector connector, Locale locale) {
        String fieldId = convert(record.getId());
        Optional<StandardInputFields> standardField = StandardInputFields.find(fieldId);
        if(standardField.isPresent()) {
            return standardField.get().createField(locale);
        }
        InputFieldBuilder builder = new InputFieldBuilder()
                .id(fieldId)
                .name(record.getName())
                .type(InputField.Type.valueOf(record.getType()));
        if(Boolean.valueOf(record.getRequired())) {
            builder.required();
        }
        return new InputFieldValueBroker(connector).buildValues(builder, fieldId);
    }
}
