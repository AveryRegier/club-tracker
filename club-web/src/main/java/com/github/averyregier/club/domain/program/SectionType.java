package com.github.averyregier.club.domain.program;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface SectionType {
    public default boolean mustBeSigned() {
        return true;
    }
    public default boolean requiredToMoveOn() {
        return true;
    }
    public default boolean requiredForGroupReward() {
        return true;
    }
    public default boolean requiredForBookReward() {
        return true;
    }
    public default boolean countsTowardsSectionMinimums() {
        return true;
    }
}
