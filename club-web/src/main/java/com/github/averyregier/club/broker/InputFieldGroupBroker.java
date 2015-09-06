package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.InputGroupRecord;
import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.adapter.InputFieldDesignatorBuilder;
import com.github.averyregier.club.domain.utility.adapter.InputFieldGroupBuilder;
import com.github.averyregier.club.domain.utility.adapter.StandardInputFields;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;

import java.util.*;
import java.util.function.Function;

import static com.github.averyregier.club.db.tables.InputGroup.INPUT_GROUP;
import static com.github.averyregier.club.domain.utility.UtilityMethods.*;

/**
 * Created by avery on 8/17/15.
 */
public class InputFieldGroupBroker extends PersistenceBroker<InputFieldGroup> {
    public InputFieldGroupBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(InputFieldGroup group, DSLContext create) {
        try {
            if (!equalsAny(create.insertInto(INPUT_GROUP)
                    .set(INPUT_GROUP.ID, group.getShortCode().getBytes())
                    .set(mapFields(group))
                    .onDuplicateKeyUpdate()
                    .set(mapFields(group))
                    .execute(), 1, 2)) {
                fail("Input Field persistence failed: " + group.getId());
            } else {
                try {
                    UUID.fromString(group.getShortCode());
                    InputFieldBroker fieldBroker = new InputFieldBroker(connector);
                    group.getFieldDesignations().forEach(v -> {
                        v.asGroup().ifPresent(childGroup -> persist(childGroup, create));
                        v.asField().ifPresent(field -> fieldBroker.persist(field, create));
                    });
                } catch (IllegalArgumentException e) {
                    // not a UUID, so its a standard field, and we don't want to persist children
                }
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(group.getName()+" "+group.getShortCode(), e);
        }
    }

    private Map<TableField<InputGroupRecord, ?>, Object> mapFields(InputFieldGroup group) {
        return JooqUtil.<InputGroupRecord>map()
                .set(INPUT_GROUP.PARENT_INPUT_GROUP_ID, applyOrNull(group.getContainer(), HasId::getShortCode))
                .set(INPUT_GROUP.NAME, group.getName())
                .set(INPUT_GROUP.THE_ORDER,
                        Optional.ofNullable(group.getContainer())
                                .map(c -> c.getFieldDesignations().indexOf(group) + 1)
                                .orElse(0))
                .build();
    }

    public Optional<InputFieldGroupBuilder> find(String id, Locale locale) {
        return query(findGroupFn(id, locale));
    }

    public InputFieldGroupBuilder find(InputFieldGroupBuilder parent, String groupId, Locale locale) {
        SortedMap<Integer, InputFieldDesignatorBuilder<?>> ordered = new TreeMap<>();

        Result<InputGroupRecord> result = query(create -> create
                .selectFrom(INPUT_GROUP)
                .where(INPUT_GROUP.PARENT_INPUT_GROUP_ID.eq(groupId.getBytes()))
                .orderBy(INPUT_GROUP.THE_ORDER)
                .fetch());
        result.stream()
                .forEach(r -> ordered.put(r.getTheOrder(), mapBuild(r, locale)));

        new InputFieldBroker(connector).recordsByGroup(groupId)
                .forEach(r -> ordered.put(r.getTheOrder(), InputFieldBroker.mapBuild(r, connector, locale)));

        ordered.values().stream().forEach(b -> parent.add(b));
        return parent;
    }

    private Function<DSLContext, Optional<InputFieldGroupBuilder>> findGroupFn(String id, Locale locale) {
        return create -> {
            InputGroupRecord record = create
                    .selectFrom(INPUT_GROUP)
                    .where(INPUT_GROUP.ID.eq(id.getBytes()))
                    .fetchOne();
            if (record == null) return Optional.empty();
            return Optional.of(mapBuild(record, locale));
        };
    }

    private InputFieldGroupBuilder mapBuild(InputGroupRecord record, Locale locale) {
        String groupId = convert(record.getId());
        Optional<StandardInputFields> standardGroup = StandardInputFields.find(groupId);
        return standardGroup.map(g->g.createGroup(locale)).orElseGet(() ->
                find(new InputFieldGroupBuilder()
                        .id(groupId)
                        .name(record.getName()),
                groupId, locale));

    }
}
