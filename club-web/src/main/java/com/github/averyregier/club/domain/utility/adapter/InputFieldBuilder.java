package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by avery on 10/2/2014.
 */
public class InputFieldBuilder implements InputFieldDesignatorBuilder<InputField> {
    private InputField.Type type;
    private String name;
    private String id;
    private List<InputField.Value> values = new ArrayList<InputField.Value>();
    private Function<Person, String> mapFn;
    private boolean required;
    private UpdateFunction updateFn;

    @Override
    public InputField build() {
        return build(null);
    }

    public InputFieldBuilder type(InputField.Type type) {
        this.type = type;
        return this;
    }

    public InputFieldBuilder name(String name) {
        this.name = name;
        return this;
    }

    public InputFieldBuilder id(String id) {
        this.id = id;
        return this;
    }

    public InputFieldBuilder value(String value) {
        return value(new InputField.Value() {
            @Override
            public String getDisplayName() {
                return value;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public boolean isDefault() {
                return false;
            }
        });
    }

    public InputField build(Later<InputFieldGroup> group) {
        return new InputFieldAdapter(
                id != null ? id : UUID.randomUUID().toString(),
                type, name, group, required, mapFn, updateFn,
                values.toArray(new InputField.Value[0]));
    }

    public InputFieldBuilder value(String value, String displayName, boolean isDefault) {
        return value(new InputField.Value() {
            @Override
            public String getDisplayName() {
                return displayName;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public boolean isDefault() {
                return isDefault;
            }
        });
    }

    public InputFieldBuilder map(Function<Person, String> mapFn) {
        this.mapFn = mapFn;
        return this;
    }

    public InputFieldBuilder value(InputField.Value value) {
        this.values.add(value);
        return this;
    }

    public InputFieldBuilder exclude(String value) {
        values.removeIf(v -> v.getValue().equals(value));
        return this;
    }


    public InputFieldBuilder required() {
        this.required = true;
        return this;
    }

    public InputFieldBuilder update(UpdateFunction updateFn) {
        this.updateFn = updateFn;
        return this;
    }

    @Override
    public InputFieldBuilder copy(InputField toCopy) {
        type(toCopy.getType());
        name(toCopy.getName());
        id(toCopy.getShortCode());
        toCopy.getValues().map(values::addAll);
        map(toCopy.getMapFn());
        update(toCopy.getUpdateFn());
        return this;
    }
}
