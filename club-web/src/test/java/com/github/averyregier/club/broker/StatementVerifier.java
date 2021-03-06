package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.utility.HasId;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by avery on 2/22/15.
 */
public class StatementVerifier {

    private StatementType type;
    private Consumer<StatementVerifier> fn;
    private String sql;
    private List<String> columns;
    private List<Object> values;

    StatementVerifier(StatementType type, Consumer<StatementVerifier> fn) {
        this.type = type;
        this.fn = fn;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(List<String> columns, List<Object> bindings, String name) {
        int i = 0;
        for (String columnName : columns) {
            if (name.equalsIgnoreCase(columnName)) {
                return (T) bindings.get(i);
            }
            i++;
        }
        throw new AssertionError("Column " + name + " not found");
    }

    public StatementType getType() {
        return type;
    }

    int bind(String sql, Collection<Object> values) {
        this.sql = sql;
        columns = type.parseColumnsFrom(sql);
        int size = columns.size();
        this.values = new ArrayList<>(values).stream()
                .limit(size)
                .peek(values::remove)
                .collect(Collectors.toList());
        return size;
    }

    void verify() {
        assertNotNull(sql);
        assertEquals(type.name(), sql.substring(0, type.name().length()).toUpperCase());

        if (fn != null) fn.accept(this);
    }

    public <T> T get(Field<T> field) {
        return getField(columns, values, field.getName());
    }

    public Object get(int index) {
        return this.values.get(index);
    }

    void assertNullFields(Field<?>... fields) {
        for(Field<?> field: fields) {
            assertNull(field.getName(), get(field));
        }
    }

    void assertUUID(HasId identifiable, Field<byte[]> field) {
        assertUUID(Optional.ofNullable(identifiable), field);
    }

    void assertUUID(Optional<? extends HasId> identifiable, Field<byte[]> field) {
        if(identifiable.isPresent()) {
            String uuid = identifiable.get().getId();
            assertUUID(uuid, field);
        } else {
            assertNull(get(field));
        }
    }

    void assertUUID(String uuid, Field<byte[]> field) {
        assertEquals(uuid, new String(get(field)));
    }

    public <T> void assertFieldEquals(T value, Field<T> field) {
        assertEquals(field.getName(), value, get(field));
    }
}
