package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.InputFieldValueRecord;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.adapter.InputFieldBuilder;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.stream.Stream;

import static com.github.averyregier.club.db.tables.InputFieldValue.INPUT_FIELD_VALUE;

/**
 * Created by avery on 8/17/15.
 */
public class InputFieldValueBroker extends Broker {
    public InputFieldValueBroker(Connector connector) {
        super(connector);
    }

    void persistValue(InputField parent, InputField.Value value, DSLContext create) {
        if(create.insertInto(INPUT_FIELD_VALUE)
                .set(INPUT_FIELD_VALUE.PARENT_INPUT_FIELD_ID, parent.getShortCode().getBytes())
                .set(INPUT_FIELD_VALUE.THE_ORDER, parent.getValues().get().indexOf(value)+1)
                .set(mapFields(value))
                .onDuplicateKeyUpdate()
                .set(mapFields(value))
                .execute() != 1) {
            fail("Input Field persistence failed: " + value.getDisplayName());
        }
    }

    private Map<TableField<InputFieldValueRecord, ?>, Object> mapFields(InputField.Value value) {
        return JooqUtil.<InputFieldValueRecord>map()
                .set(INPUT_FIELD_VALUE.NAME, value.getDisplayName())
                .set(INPUT_FIELD_VALUE.THE_VALUE, value.getValue())
                .set(INPUT_FIELD_VALUE.IS_DEFAULT, value.isDefault() ? "T" : "F")
                .build();
    }

    InputFieldBuilder buildValues(InputFieldBuilder builder, String fieldId) {
        recordsByField(fieldId).forEach(
                vRecord -> mapBuild(builder, vRecord)
        );
        return builder;
    }

    private Stream<InputFieldValueRecord> recordsByField(String fieldId) {
        return query(create -> create
                .selectFrom(INPUT_FIELD_VALUE)
                .where(INPUT_FIELD_VALUE.PARENT_INPUT_FIELD_ID.eq(fieldId.getBytes()))
                .orderBy(INPUT_FIELD_VALUE.THE_ORDER)
                .fetch()).stream();
    }

    private InputFieldBuilder mapBuild(InputFieldBuilder builder, InputFieldValueRecord record) {
        return builder.value(record.getTheValue(), record.getName(), Boolean.valueOf(record.getIsDefault()));
    }
}
