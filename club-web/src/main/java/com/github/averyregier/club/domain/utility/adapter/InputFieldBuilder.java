package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.ChildBuilder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avery on 10/2/2014.
 */
public class InputFieldBuilder implements Builder<InputField>, ChildBuilder<InputFieldGroup, InputFieldDesignator> {
    private InputField.Type type;
    private String name;
    private String id;
    private List<InputField.Value> values = new ArrayList<InputField.Value>();

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
        this.values.add(new InputField.Value() {
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
        return this;
    }

    public InputField build(Later<InputFieldGroup> group) {
        return new InputFieldAdapter(id, type, name, group, values.toArray(new InputField.Value[0]));
    }

    public InputFieldBuilder value(String value, String displayName, boolean isDefault) {
        this.values.add(new InputField.Value() {
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
        return this;
    }
}
