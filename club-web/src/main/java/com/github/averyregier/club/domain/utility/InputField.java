package com.github.averyregier.club.domain.utility;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.utility.adapter.UpdateFunction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by avery on 10/2/2014.
 */
public interface InputField extends InputFieldDesignator {

    enum Type {
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
                } catch(IllegalArgumentException|NullPointerException e) {
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
        },
        date {
            @Override
            Date validate(String input) {
                if(input == null) return null;
                try {
                    return dateFormat.get().parse(input.trim());
                } catch (ParseException e) {
                    return null;
                }
            }
        };

        Object validate(String input) {
            return UtilityMethods.killWhitespace(input);
        }

        public static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd");
            }
        };

    }

    Type getType();

    Optional<List<Value>> getValues();

    default Optional<Object> validate(String input) {
        return Optional.ofNullable(getType().validate(input));
    }

    boolean isRequired();

    String map(Person person);
    UpdateFunction getUpdateFn();
    Function<Person, String> getMapFn();

    interface Value {
        String getDisplayName();
        String getValue();
        boolean isDefault();
    }
}
