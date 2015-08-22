package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by avery on 9/30/2014.
 */
public class InputFieldAdapter implements InputField {

    private Type type;
    private String id;
    private String name;
    private final Later<InputFieldGroup> group;
    private final boolean required;
    private Function<Person, String> mapFn;
    private final BiConsumer<Person, Object> updateFn;
    private Optional<List<Value>> values = Optional.empty();


    InputFieldAdapter(String id, Type type, String name, Later<InputFieldGroup> group,
                      boolean required, Function<Person, String> mapFn, BiConsumer<Person, Object> updateFn,
                      Value... values)
    {
        this.type = type;
        this.id = id;
        this.name = name;
        this.group = group;
        this.required = required;
        this.mapFn = mapFn;
        this.updateFn = updateFn;
        if (values != null && values.length > 0) {
            this.values = Optional.of(Arrays.asList(values));
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<InputFieldGroup> asGroup() {
        return Optional.empty();
    }

    @Override
    public Optional<InputField> asField() {
        return Optional.of(this);
    }

    @Override
    public String getId() {
        if(group != null) {
            return group.get().getId()+"."+id;
        }
        return id;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public boolean isField() {
        return true;
    }

    @Override
    public String getShortCode() {
        return id;
    }

    @Override
    public Optional<List<Value>> getValues() {
        return values;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String map(Person person) {
        return mapFn.apply(person);
    }

    @Override
    public InputFieldGroup getContainer() {
        return group != null ? group.get() : null;
    }

    @Override
    public Optional<Object> validateFromParentMap(Map<String, String> map) {
        return asField().get().validate(map.get(getShortCode()));
    }

    @Override
    public void update(Person person, Object results) {
        if(updateFn != null) updateFn.accept(person, results);
    }

    @Override
    public InputFieldBuilder copy() {
        return new InputFieldBuilder().copy(this);
    }
}
