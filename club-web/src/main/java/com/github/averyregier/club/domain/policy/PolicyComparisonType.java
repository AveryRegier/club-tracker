package com.github.averyregier.club.domain.policy;

import com.github.averyregier.club.domain.club.Person;

/**
 * Created by avery on 9/5/2014.
 */
public enum PolicyComparisonType {
    BOOLEAN {
        @Override
        public Object validate(String value) {
            return Boolean.parseBoolean(value);
        }
    },
    MINIMUM {
        @Override
        public Object validate(String value) {
            return Double.parseDouble(value);
        }
    },
    MAXIMUM {
        @Override
        public Object validate(String value) {
            return Double.parseDouble(value);
        }
    },
    GENDER {
        @Override
        public Object validate(String value) {
            return Person.Gender.valueOf(value);
        }
    };

    public abstract Object validate(String value);
}
