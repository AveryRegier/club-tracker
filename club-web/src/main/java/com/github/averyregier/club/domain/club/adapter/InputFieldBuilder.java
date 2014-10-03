package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.builder.Builder;
import com.github.averyregier.club.domain.builder.Later;
import com.github.averyregier.club.domain.club.InputField;
import com.github.averyregier.club.domain.club.InputFieldGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avery on 10/2/2014.
 */
public class InputFieldBuilder implements Builder<InputField> {
    private InputField.Type type;
    private String name;
    private String id;
    private List<String> values = new ArrayList<>();

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
        this.values.add(value);
        return this;
    }

    public InputField build(Later<InputFieldGroup> group) {
        return new InputFieldAdapter(id, type, name, group, values.toArray());
    }
}
