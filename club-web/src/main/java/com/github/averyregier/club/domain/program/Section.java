package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Contained;
import com.github.averyregier.club.domain.utility.UtilityMethods;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by avery on 9/6/2014.
 */
public interface Section extends Comparable<Section>, Contained<SectionGroup> {
    SectionType getSectionType();
    SectionGroup getGroup();
    Set<Award> getAwards();
    int sequence();

    default Set<Award> getAwards(AccomplishmentLevel type) {
        return getAwards().stream()
                .filter(t->t.getAccomplishmentLevel() == type)
                .filter(isValidAward())
                .collect(UtilityMethods.toLinkedSet());
    }

    default Optional<Award> findAward(String name) {
        return getAwards().stream()
                .filter(a->a.getName().equals(name))
                .findFirst();
    }

    default String getId() {
        return getContainer().getId()+":"+getShortCode();
    }


    default String getSectionTitle() {
        return getSectionCode();
    }

    default String getSectionCode() {
        return getGroup().getBook().getMwhCode()+"-"+getGroup().getShortCode()+"."+getShortCode();
    }

    default boolean isAfter(Section section) {
        if(getContainer().getBook().sequence() == section.getContainer().getBook().sequence()) {
            if(getContainer().sequence() == section.getContainer().sequence()) {
                return sequence() > section.sequence();
            } else {
                return getContainer().sequence() > section.getContainer().sequence();
            }
        } else {
            return getContainer().getBook().sequence() > section.getContainer().getBook().sequence();
        }
    }

    default boolean isBefore(Section section) {
        if(getContainer().getBook().sequence() == section.getContainer().getBook().sequence()) {
            if(getContainer().sequence() == section.getContainer().sequence()) {
                return sequence() < section.sequence();
            } else {
                return getContainer().sequence() < section.getContainer().sequence();
            }
        } else {
            return getContainer().getBook().sequence() < section.getContainer().getBook().sequence();
        }
    }

    @Override
    default int compareTo(Section o) {
        return ((sequence() - o.sequence())*10)+
                (getSectionType().ordinal() - o.getSectionType().ordinal());
    }

    default Predicate<Award> isValidAward() {
        return t-> getSectionType().requiredFor(t.getAccomplishmentLevel());
    }
}
