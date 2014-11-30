package com.github.averyregier.club.domain.utility;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.program.AgeGroup;

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
        },
        text,
        gender {
            @Override
            Object validate(String input) {
                try {
                    return Person.Gender.valueOf(input);
                } catch(IllegalArgumentException|NullPointerException e) {
                    return null;
                }
            }
        },
        email {
            @Override
            Object validate(String input) {
                return input != null && input.matches("\\S*@\\S*\\.\\S*") ? input : null;
            }
        },
        ageGroup {
            @Override
            Object validate(String input) {
                try {
                    return AgeGroup.DefaultAgeGroup.valueOf(input);
                } catch(IllegalArgumentException e) {
                    return null;
                }
            }
        },
        action {
            @Override
            Object validate(String input) {
                try {
                    return Action.valueOf(input);
                } catch(IllegalArgumentException|NullPointerException e) {
                    return null;
                }
            }
        };

        Object validate(String input) {
            return input;
        }
    }

    Type getType();

    Optional<List<Value>> getValues();

    public default Optional<Object> validate(String input) {
        return Optional.ofNullable(getType().validate(input));
    }

    public boolean isRequired();

    public String map(Person person);

    public interface Value {
        public String getDisplayName();
        public String getValue();
        public boolean isDefault();
    }
}
