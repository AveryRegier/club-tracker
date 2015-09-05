package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.InputField;

import java.util.Map;

/**
 * Created by avery on 8/27/15.
 */
public interface Registered {

    void setValue(InputField field, String value);
    String getValue(InputField field);

    Map<InputField, String> getValues();
}
