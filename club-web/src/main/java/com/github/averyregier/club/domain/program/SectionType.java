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
    public default boolean requiredForGroupAward() {
        return true;
    }
    public default boolean requiredForBookAward() {
        return true;
    }
    public default boolean countsTowardsSectionMinimums() {
        return true;
    }
}
