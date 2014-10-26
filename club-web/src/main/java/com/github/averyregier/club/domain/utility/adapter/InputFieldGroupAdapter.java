package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by avery on 10/1/2014.
 */
public class InputFieldGroupAdapter implements InputFieldGroup {
    private final String id;
    private final String name;
    private Later<InputFieldGroup> parent;
    private final List<InputFieldDesignator> children;
    private final BiFunction<Person, Map<String, Object>, Optional<Object>> validationFn;
    private final Function<Person, Map<String, String>> mapFn;

    InputFieldGroupAdapter(String id, String name,
                           Later<InputFieldGroup> parent,
                           List<InputFieldDesignator> children,
                           BiFunction<Person, Map<String, Object>, Optional<Object>> validationFn,
                           Function<Person, Map<String, String>> mapFn)
    {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.validationFn = validationFn;
        this.mapFn = mapFn != null ? mapFn : defaultMapper();
        this.children = new ArrayList<>(children);
    }

    private Function<Person, Map<String, String>> defaultMapper() {
        return p->{
            Map<String, String> model = new HashMap<String,String>();
            for(InputFieldDesignator d: getFieldDesignations()) {
                if(d.asField().isPresent()) {
                    model.put(d.getShortCode(), d.asField().get().map(p));
                } else {
                    UtilityMethods.putAll(
                            model,
                            d.getShortCode(),
                            d.asGroup().get().map(p));
                }
            }
            return model;
        };
    }

    @Override
    public String getId() {
        if(parent != null) {
            return parent.get().getId()+"."+id;
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
    public boolean isGroup() {
        return true;
    }

    @Override
    public Optional<Object> validate(Map<String, String> map) {
        Map<String, Object> results = new HashMap<>();
        for(InputFieldDesignator d: getFieldDesignations()) {
            Optional<Object> dResult;
            if(d.asField().isPresent())  {
                dResult = d.asField().get().validate(map.get(d.getShortCode()));
            } else {
                dResult = d.asGroup().get().validate(UtilityMethods.subMap(d.getShortCode(), map));
            }
            if(!dResult.isPresent()) return Optional.empty();
            results.put(d.getShortCode(), dResult.get());
        }
        return validationFn != null ? validationFn.apply(null, results) : Optional.empty();
    }

    @Override
    public Map<String, String> map(Person person) {
        return mapFn.apply(person);
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
