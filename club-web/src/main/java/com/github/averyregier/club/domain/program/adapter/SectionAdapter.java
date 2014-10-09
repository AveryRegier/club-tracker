package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
* Created by avery on 9/10/2014.
*/
class SectionAdapter implements Section {
    private String shortCode;
    private final List<Later<Award>> awards;
    private Later<SectionGroup> group;
    private SectionType sectionType;
    private int sequence;

    public SectionAdapter(Later<SectionGroup> group, SectionType sectionType,
                          int sequence, String shortCode, List<Later<Award>> awards) {
        this.group = group;
        this.sectionType = sectionType;
        this.sequence = sequence;
        this.shortCode = shortCode;
        this.awards = awards;
    }

    @Override
    public SectionType getSectionType() {
        return sectionType;
    }

    @Override
    public SectionGroup getGroup() {
        return group.get();
    }

    @Override
    public Set<Award> getAwards() {
        return awards.stream().map(r->r.get())
                .filter(isValidAward())
                .collect(UtilityMethods.toLinkedSet());
    }

    @Override
    public Set<Award> getAwards(AwardType type) {
        return getAwards().stream()
                .filter(t->t.getAwardType() == type)
                .filter(isValidAward())
                .collect(UtilityMethods.toLinkedSet());
    }

    private Predicate<Award> isValidAward() {
        return t-> getSectionType().requiredFor(t.getAwardType());
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public String getId() {
        return getContainer().getId()+":"+getShortCode();
    }

    @Override
    public String getShortCode() {
        return shortCode;
    }

    @Override
    public int compareTo(Section o) {
        return sequence() - o.sequence();
    }

    @Override
    public SectionGroup getContainer() {
        return group.get();
    }
}
