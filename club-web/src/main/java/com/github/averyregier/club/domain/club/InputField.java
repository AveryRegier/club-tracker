package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.Contained;
import com.github.averyregier.club.domain.HasId;

import java.util.List;
import java.util.Optional;

/**
 * Created by avery on 10/2/2014.
 */
public interface InputField extends Contained<InputFieldGroup> {

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

    String getName();

    Optional<List<String>> getValues();

    public default Optional<Object> validate(String input) {
        return Optional.ofNullable(getType().validate(input));
    }
}
