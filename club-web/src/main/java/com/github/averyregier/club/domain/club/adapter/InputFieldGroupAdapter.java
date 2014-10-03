package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.builder.Later;
import com.github.averyregier.club.domain.club.InputField;
import com.github.averyregier.club.domain.club.InputFieldGroup;

import java.util.List;

/**
 * Created by avery on 10/1/2014.
 */
public class InputFieldGroupAdapter implements InputFieldGroup {
    private final String id;
    private final String name;
    private Later<InputFieldGroup> parent;
    private final List<InputField> fields;
    private final List<InputFieldGroup> groups;

    InputFieldGroupAdapter(String id, String name, Later<InputFieldGroup> parent, List<InputFieldGroup> groups, List<InputField> fields) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.groups = groups;
        this.fields = fields;
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
    public List<InputField> getFields() {
        return fields;
    }

    @Override
    public List<InputFieldGroup> getGroups() {
        return groups;
    }

    @Override
    public InputFieldGroup getContainer() {
        return parent != null ? parent.get() : null;
    }
}
