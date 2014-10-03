package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by avery on 10/1/2014.
 */
public class InputFieldGroupAdapter implements InputFieldGroup {
    private final String id;
    private final String name;
    private Later<InputFieldGroup> parent;
    private final List<InputFieldDesignator> children;

    InputFieldGroupAdapter(String id, String name, Later<InputFieldGroup> parent, List<InputFieldDesignator> children) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.children = new ArrayList<>(children);
    }

    @Override
    public String getId() {
        if(parent != null) {
            return parent.get().getId()+":"+id;
        }
        return id;
    }

    @Override
    public String getShortCode() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<InputFieldGroup> asGroup() {
        return Optional.of(this);
    }

    @Override
    public Optional<InputField> asField() {
        return Optional.empty();
    }

    @Override
    public List<InputField> getFields() {
        return children.stream()
                .filter(c -> c.asField().isPresent())
                .map(c -> c.asField().get())
                .collect(Collectors.toList());
    }

    @Override
    public List<InputFieldGroup> getGroups() {
        return children.stream()
                .filter(c->c.asGroup().isPresent())
                .map(c->c.asGroup().get())
                .collect(Collectors.toList());
    }

    @Override
    public List<InputFieldDesignator> getFieldDesignations() {
        return children;
    }

    @Override
    public InputFieldGroup getContainer() {
        return parent != null ? parent.get() : null;
    }
}
