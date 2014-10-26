package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.InputFieldDesignator;

import java.util.List;
import java.util.Map;

/**
 * Created by avery on 9/5/2014.
 */
public interface RegistrationInformation {

    public List<InputFieldDesignator> getForm();
    public Map<String, String> getFields();
}
