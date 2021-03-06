package com.github.averyregier.club.domain.utility;

import com.github.averyregier.club.domain.club.Person;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by avery on 10/2/2014.
 */
public interface InputFieldGroup extends InputFieldDesignator {
    String getName();

    List<InputField> getFields();

    List<InputFieldGroup> getGroups();

    List<InputFieldDesignator> getFieldDesignations();

    default Optional<InputFieldDesignator> find(String child, String... descendants) {
        String[] next = null;
        if(descendants != null && descendants.length > 0) {
            int l = descendants.length - 1;
            next = new String[l];
            System.arraycopy(descendants, 1, next, 0, l);
        }
        for(InputFieldDesignator d: getFieldDesignations()) {
            if(d.getShortCode().equals(child)) {
                if(next != null ) {
                    if(d.asGroup().isPresent()) {
                        return d.asGroup().get().find(descendants[0], next);
                    } else return Optional.empty();
                } else {
                    return Optional.of(d);
                }
            }
        }
        return Optional.empty();
    }

    Optional<Object> validate(Map<String, String> map);

    Map<String, String> map(Person person);

    Optional<InputField> findField(String descendant);

    @Override
    default Collection<InputField> getLeaves() {
        return getFieldDesignations().stream()
                .map(InputFieldDesignator::getLeaves)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}