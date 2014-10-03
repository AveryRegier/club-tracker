package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.Contained;

import java.util.Optional;

/**
 * Created by rx39789 on 10/3/2014.
 */
public interface InputFieldDesignator extends Contained<InputFieldGroup> {
    public String getName();
    public Optional<InputFieldGroup> asGroup();
    public Optional<InputField> asField();
}
