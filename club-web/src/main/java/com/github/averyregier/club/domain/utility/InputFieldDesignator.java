package com.github.averyregier.club.domain.utility;

import com.github.averyregier.club.domain.club.Person;

import java.util.Map;
import java.util.Optional;

/**
 * Created by avery on 10/3/2014.
 */
public interface InputFieldDesignator extends Contained<InputFieldGroup> {
    public String getName();
    public Optional<InputFieldGroup> asGroup();
    public Optional<InputField> asField();
    public default boolean isGroup() {
        return false;
    }

    public default boolean isField() {
        return false;
    }

    public Optional<Object> validateFromParentMap(Map<String, String> map);
    public void update(Person person, Object results);
}
