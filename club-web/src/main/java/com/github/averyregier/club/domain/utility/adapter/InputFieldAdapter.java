package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.Registered;
import com.github.averyregier.club.domain.club.RegistrationSection;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;

/**
 * Created by avery on 9/30/2014.
 */
public class InputFieldAdapter implements InputField {

    private Type type;
    private String id;
    private String name;
    private final Later<InputFieldGroup> group;
    private final boolean required;
    private Function<Person, String> mapFn;
    private final UpdateFunction updateFn;
    private Optional<List<Value>> values = Optional.empty();


    InputFieldAdapter(String id, Type type, String name, Later<InputFieldGroup> group,
                      boolean required, Function<Person, String> mapFn, UpdateFunction updateFn,
                      Value... values)
    {
        this.type = type;
        this.id = id;
        this.name = name;
        this.group = group;
        this.required = required;
        this.mapFn = mapFn;
        this.updateFn = updateFn != null ? updateFn : extraRegFieldsUpdater();
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
    public boolean isRequired() {
        return required;
    }

    @Override
    public String map(Person person) {
        return applyOrDefault(person,
                mapFn,
                extraFieldsMapFn());
    }

    private Function<Person, String> extraFieldsMapFn() {
        return p -> findRegistered(this, p).map(a->a.getValue(this)).orElse(null);
    }

    @Override
    public InputFieldGroup getContainer() {
        return applyOrNull(group, Later::get);
    }

    @Override
    public Optional<Object> validateFromParentMap(Map<String, String> map) {
        return asField().get().validate(map.get(getShortCode()));
    }

    @Override
    public void update(Person person, Object results) {
        if(updateFn != null) updateFn.update(this, person, results);
    }

    @Override
    public InputFieldBuilder copy() {
        return new InputFieldBuilder().copy(this);
    }

    @Override
    public UpdateFunction getUpdateFn() {
        return updateFn;
    }

    @Override
    public Function<Person, String> getMapFn() {
        return mapFn;
    }

    private static UpdateFunction extraRegFieldsUpdater() {
        return (d, p, m) -> findRegistered(d, p).ifPresent(a -> a.setValue(d.asField().get(), orNull(m, Object::toString)));
    }

    private static Optional<Registered> findRegistered(InputFieldDesignator d, Person p) {
        InputFieldGroup container = d.getContainer();
        if(container != null) {
            try {
                return Optional.of(RegistrationSection.findSection(getTopContainer(container).getName()).find(p));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(d.getName(), e);
            }
        }

        return Optional.empty();
    }

    private static InputFieldGroup getTopContainer(InputFieldGroup container) {
        if(container == null) throw new IllegalStateException("Field is not contained");
        InputFieldGroup parent = container.getContainer();
        if(parent != null) {
            return getTopContainer(parent);
        }
        return container;
    }

    @Override
    public boolean equals(Object obj) {
        return getShortCode().equals(((InputFieldDesignator) obj).getShortCode());
    }

    @Override
    public int hashCode() {
        return getShortCode().hashCode();
    }
}
