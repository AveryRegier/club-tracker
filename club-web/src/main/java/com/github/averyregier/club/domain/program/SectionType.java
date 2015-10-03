package com.github.averyregier.club.domain.program;

/**
 * Created by avery on 9/6/2014.
 */
public interface SectionType {
    public default boolean mustBeSigned() {
        return true;
    }
    public default boolean requiredToMoveOn() {
        return true;
    }
    public default boolean requiredFor(AccomplishmentLevel type) {
        return true;
    }
    public default boolean countsTowardsSectionMinimums() {
        return true;
    }

    default boolean isExtraCredit() {
        return !requiredFor(AccomplishmentLevel.book);
    }
}
