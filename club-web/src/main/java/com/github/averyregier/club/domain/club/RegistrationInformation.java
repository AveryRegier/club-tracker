package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/5/2014.
 */
public interface RegistrationInformation {

    List<InputFieldDesignator> getForm();

    default List<InputField> getLeaves() {
        return getForm().stream()
                .map(InputFieldDesignator::getLeaves)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    Map<String, String> getFields();

    default Map<InputFieldDesignator, Object> validate() {
        Map<InputFieldDesignator, Object> results = new LinkedHashMap<>();

        Map<String, String> fields = getFields();
        for(InputFieldDesignator section: getForm()) {
            Optional<Object> result = section.validateFromParentMap(fields);
            result.ifPresent(o -> results.put(section, o));
        }
        return results;
    }

    Family register(Person person);

    Family register();
}
