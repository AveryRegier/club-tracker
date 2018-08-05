package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.SectionType;

import java.util.Optional;

/**
* Created by avery on 9/6/2014.
*/
public enum CubbiesSectionTypes implements SectionType {
    start {
        @Override
        public String getReadableName() {
            return greenBearHug.getReadableName();
        }

        @Override
        public boolean requiredForStart() {
            return true;
        }

        @Override
        public Optional<String> getAwardName() {
            return greenBearHug.getAwardName();
        }

        @Override
        public String getCssClass() {
            return greenBearHug.getCssClass();
        }
    },
    greenBearHug {
        @Override
        public String getReadableName() {
            return "Bear Hug";
        }

        @Override
        public Optional<String> getAwardName() {
            return Optional.of("Green Apple");
        }
    },
    redBearHug {
        @Override
        public String getReadableName() {
            return "Bear Hug";
        }

        @Override
        public Optional<String> getAwardName() {
            return Optional.of("Red Apple");
        }
    },
    specialDay {
        @Override
        public boolean requiredFor(AccomplishmentLevel type) {
            return false;
        }

        @Override
        public Optional<String> getAwardName() {
            return Optional.empty();
        }
    },
    review {
        @Override
        public boolean isScheduled() {
            return false;
        }
    },
    underTheAppleTree {
        @Override
        public boolean isExtraCredit() {
            return true;
        }

        @Override
        public boolean isScheduled() {
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

    @Override
    public boolean isScheduled() {
        return true;
    }

    @Override
    public String getReadableName() {
        return SectionType.super.getReadableName();
    }

    @Override
    public String getCssClass() {
        return SectionType.super.getCssClass();
    }

    public Optional<String> getAwardName() {
        return Optional.of(getReadableName());
    }
}
