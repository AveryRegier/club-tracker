package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.builder.Builder;
import com.github.averyregier.club.domain.builder.Later;
import com.github.averyregier.club.domain.club.InputFieldGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by avery on 10/2/2014.
 */
public class InputFieldGroupBuilder implements Builder<InputFieldGroup> {
    private String id;
    private String name;
    private List<InputFieldBuilder> fields = new ArrayList<>();
    private List<InputFieldGroupBuilder> groups = new ArrayList<>();

    public InputFieldGroupBuilder id(String id) {
        this.id = id;
        return this;
    }

    public InputFieldGroupBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public InputFieldGroup build() {
        return build(null);
    }

    private InputFieldGroup build(Later<InputFieldGroup> parent) {
        Later<InputFieldGroup> later = new Later<>();
        InputFieldGroupAdapter group = new InputFieldGroupAdapter(
                id,
                name,
                parent,
                groups.stream().map(g -> g.build(later)).collect(Collectors.toList()),
                fields.stream().map(f -> f.build(later)).collect(Collectors.toList()));
        later.set(group);
        return group;
    }

    public InputFieldGroupBuilder field(UnaryOperator<InputFieldBuilder> fn) {
        fields.add(fn.apply(new InputFieldBuilder()));
        return this;
    }

    public InputFieldGroupBuilder group(UnaryOperator<InputFieldGroupBuilder> fn) {
        groups.add(fn.apply(new InputFieldGroupBuilder()));
        return this;
    }
}
