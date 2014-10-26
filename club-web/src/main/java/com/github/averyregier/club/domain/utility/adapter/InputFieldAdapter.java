package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by avery on 9/30/2014.
 */
public class InputFieldAdapter implements InputField {

    private Type type;
    private String id;
    private String name;
    private final Later<InputFieldGroup> group;
    private Function<Person, String> mapFn;
    private Optional<List<Value>> values = Optional.empty();


    InputFieldAdapter(String id, Type type, String name, Later<InputFieldGroup> group, Function<Person, String> mapFn, Value... values) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.group = group;
        this.mapFn = mapFn;
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
    public String map(Person person) {
        return mapFn.apply(person);
    }

    @Override
    public InputFieldGroup getContainer() {
        return group != null ? group.get() : null;
    }
}
