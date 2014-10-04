package com.github.averyregier.club.domain.utility;

import java.util.List;
import java.util.Optional;

/**
 * Created by avery on 10/2/2014.
 */
public interface InputField extends InputFieldDesignator {

    public enum Type {
        integer {
            @Override
            Object validate(String input) {
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }, text;

        Object validate(String input) {
            return input;
        }
    }

    Type getType();

    Optional<List<Value>> getValues();

    public default Optional<Object> validate(String input) {
        return Optional.ofNullable(getType().validate(input));
    }

    public interface Value {
        public String getDisplayName();
        public String getValue();
        public boolean isDefault();
    }
}
