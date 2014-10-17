package com.github.averyregier.club.domain.utility;

import java.util.Optional;

/**
 * Created by avery on 10/3/2014.
 */
public interface InputFieldDesignator extends Contained<InputFieldGroup> {
    public String getName();
    public Optional<InputFieldGroup> asGroup();
    public Optional<InputField> asField();

}
