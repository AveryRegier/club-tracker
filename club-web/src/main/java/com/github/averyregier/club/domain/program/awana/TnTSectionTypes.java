package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.SectionType;

/**
* Created by rx39789 on 9/6/2014.
*/
public enum TnTSectionTypes {
    parent(new SectionType() {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }

        @Override
        public boolean countsTowardsSectionMinimums() {
            return false;
        }
    }),
    regular(new SectionType() {}),
    friend(new SectionType() {
        @Override
        public boolean requiredToMoveOn() {
            return false;
        }
    }),
    extaCredit(new SectionType() {
        @Override
        public boolean requiredForBookReward() {
            return false;
        }

        @Override
        public boolean requiredToMoveOn() {
            return false;
        }
    });

    private SectionType sectionType;

    TnTSectionTypes(SectionType sectionType) {
        this.sectionType = sectionType;
    }

    public SectionType get() {
        return sectionType;
    }

}
