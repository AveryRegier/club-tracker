package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.SectionType;

/**
* Created by avery on 9/6/2014.
*/
public enum TnTMissionSectionTypes implements SectionType {
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
    regular {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }
    },
    go {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }
    },
    review {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }

    },
    extraCredit {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }

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


    @Override
    public String getReadableName() {
        return SectionType.super.getReadableName();
    }
}
