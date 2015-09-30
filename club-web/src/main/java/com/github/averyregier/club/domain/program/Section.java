package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Contained;

import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface Section extends Comparable<Section>, Contained<SectionGroup> {
    public SectionType getSectionType();
    public SectionGroup getGroup();

    public Set<Award> getAwards();
    public Set<Award> getAwards(AccomplishmentLevel type);

    public default Optional<Award> findAward(String name) {
        return getAwards().stream()
                .filter(a->a.getName().equals(name))
                .findFirst();
    }

    int sequence();

    String getId();

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
}
