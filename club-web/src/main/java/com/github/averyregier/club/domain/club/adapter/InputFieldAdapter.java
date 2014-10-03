package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.builder.Later;
import com.github.averyregier.club.domain.club.InputField;
import com.github.averyregier.club.domain.club.InputFieldGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/30/2014.
 */
public class InputFieldAdapter implements InputField {

    private Type type;
    private String id;
    private String name;
    private final Later<InputFieldGroup> group;
    private Optional<List<Object>> values = Optional.empty();


    InputFieldAdapter(String id, Type type, String name, Later<InputFieldGroup> group, Object... values) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.group = group;
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
            return group.get().getId()+":"+id;
        }
        return id;
    }

    @Override
    public String getShortCode() {
        return id;
    }

    @Override
    public Optional<List<String>> getValues() {
        return values.map(convertToStringList());
    }

    private Function<List<?>, List<String>> convertToStringList() {
        return l -> l.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public InputFieldGroup getContainer() {
        return group.get();
    }
}
