package com.github.averyregier.club.domain.program;

import org.apache.commons.lang.StringUtils;

/**
 * Created by avery on 9/6/2014.
 */
public interface SectionType {
    default boolean mustBeSigned() {
        return true;
    }

    default boolean requiredToMoveOn() {
        return true;
    }

    default boolean requiredFor(AccomplishmentLevel type) {
        return true;
    }

    default boolean countsTowardsSectionMinimums() {
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

    int ordinal();

    default String getCssClass() {
        return toString();
    }
}
