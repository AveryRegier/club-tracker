package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;

/**
 * Created by avery on 8/31/15.
 */
@FunctionalInterface
public interface UpdateFunction {
    void update(InputFieldDesignator designator, Person person, Object value);
}
