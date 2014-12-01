package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.InputFieldDesignator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by avery on 9/5/2014.
 */
public interface RegistrationInformation {

    public List<InputFieldDesignator> getForm();
    public Map<String, String> getFields();

    public default Map<InputFieldDesignator, Object> validate() {
        Map<InputFieldDesignator, Object> results = new LinkedHashMap<>();

        Map<String, String> fields = getFields();
        for(InputFieldDesignator section: getForm()) {
            Optional<Object> result = section.validateFromParentMap(fields);
            if(result.isPresent()) {
                results.put(section, result.get());
            }
        }
        return results;
    }
}
