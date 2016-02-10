package com.github.averyregier.club.domain.program;

import org.apache.commons.lang.StringUtils;

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

    default String getReadableName() {
        return StringUtils.capitalize(StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(toString()),
                ' '
        ));
    }
}
