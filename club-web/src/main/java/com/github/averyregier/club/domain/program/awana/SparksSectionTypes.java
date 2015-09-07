package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.SectionType;

/**
* Created by avery on 9/6/2014.
*/
public enum SparksSectionTypes implements SectionType {
    regular,
    friend {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }
    },
    group {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }
    },
    review {
        @Override
        public boolean requiredFor(AccomplishmentLevel type) {
            return !type.isBook();
        }
    },
    extraCredit {
        @Override
        public boolean requiredFor(AccomplishmentLevel type) {
            return !type.isBook();
        }
    };

    public SectionType get() {
        return this;
    }

    @Override
    public String toString() {
        return name();
    }
}
