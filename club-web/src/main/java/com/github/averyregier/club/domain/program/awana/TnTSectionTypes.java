package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.SectionType;

/**
* Created by rx39789 on 9/6/2014.
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

        @Override
        public String toString() {
            return parent.name();
        }
    },
    regular {
        @Override
        public String toString() {
            return regular.name();
        }
    },
    friend {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }

        @Override
        public String toString() {
            return friend.name();
        }
    },
    extaCredit {
        @Override
        public boolean requiredForBookReward() {
            return false;
        }

        @Override
        public boolean requiredToMoveOn() {
            return false;
        }

        @Override
        public String toString() {
            return extaCredit.name();
        }
    };

    public SectionType get() {
        return this;
    }

}
