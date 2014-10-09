package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.SectionType;

/**
* Created by avery on 9/6/2014.
*/
public enum TnTSectionTypes implements SectionType {
    parent {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }

        @Override
        public boolean countsTowardsSectionMinimums() {
            return false;
        }
    },
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
    extaCredit {
        @Override
        public boolean requiredForBookAward() {
            return false;
        }

        @Override
        public boolean requiredToMoveOn() {
            return false;
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
