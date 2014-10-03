package com.github.averyregier.club.domain.utility;

import java.util.List;

/**
 * Created by avery on 10/2/2014.
 */
public interface InputFieldGroup extends InputFieldDesignator {
    String getName();

    List<InputField> getFields();

    List<InputFieldGroup> getGroups();

    List<InputFieldDesignator> getFieldDesignations();
}
