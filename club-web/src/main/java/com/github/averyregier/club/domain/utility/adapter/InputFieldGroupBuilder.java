package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.ChildBuilder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by avery on 10/2/2014.
 */
public class InputFieldGroupBuilder
  implements InputFieldDesignatorBuilder<InputFieldGroup>
{
    private String id;
    private String name;
    private List<ChildBuilder<InputFieldGroup, InputFieldDesignator>> children = new ArrayList<>();
    private Function<Map<String, Object>, Optional<Object>> validationFn;
    private Function<Person, Map<String, String>> mapFn;
    private UpdateFunction updateFn;

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

    public InputFieldGroup build(Later<InputFieldGroup> parent) {
        Later<InputFieldGroup> later = new Later<>();
        InputFieldGroupAdapter group = new InputFieldGroupAdapter(
                id != null ? id : UUID.randomUUID().toString(),
                name,
                parent,
                children.stream().map(g -> g.build(later)).collect(Collectors.toList()),
                validationFn,
                mapFn,
                updateFn
        );
        later.set(group);
        return group;
    }

    public InputFieldGroupBuilder field(UnaryOperator<InputFieldBuilder> fn) {
        children.add(fn.apply(new InputFieldBuilder()));
        return this;
    }

    public InputFieldGroupBuilder validate(Function<Map<String, Object>, Optional<Object>> validationFn) {
        this.validationFn = validationFn;
        return this;
    }

    public InputFieldGroupBuilder group(UnaryOperator<InputFieldGroupBuilder> fn) {
        return group(fn.apply(new InputFieldGroupBuilder()));
    }

    public InputFieldGroupBuilder group(InputFieldGroupBuilder groupBuilder) {
        children.add(groupBuilder);
        return this;
    }

    public InputFieldGroupBuilder field(InputFieldBuilder fieldBuilder) {
        children.add(fieldBuilder);
        return this;
    }

    public InputFieldGroupBuilder map(Function<Person, Map<String,String>> mapFn) {
        this.mapFn = mapFn;
        return this;
    }

    public InputFieldGroupBuilder update(UpdateFunction updateFn) {
        this.updateFn = updateFn;
        return this;
    }

    @Override
    public InputFieldGroupBuilder copy(InputFieldGroup toCopy) {
        id(toCopy.getShortCode());
        name(toCopy.getName());
        this.updateFn = ((InputFieldGroupAdapter)toCopy).updateFn;
        this.mapFn = ((InputFieldGroupAdapter)toCopy).mapFn;
        this.validationFn = ((InputFieldGroupAdapter)toCopy).validationFn;
        this.children = toCopy.getFieldDesignations().stream()
                .map(InputFieldDesignator::copy)
                .collect(Collectors.toList());
        return this;
    }

    public InputFieldGroupBuilder add(ChildBuilder<InputFieldGroup, InputFieldDesignator> copy) {
        this.children.add(copy);
        return this;
    }
}
