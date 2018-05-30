package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.List;
import java.util.Set;

/**
* Created by avery on 9/10/2014.
*/
class SectionAdapter implements Section {
    private String shortCode;
    private final List<Later<Award>> awards;
    private String name;
    private Later<SectionGroup> group;
    private SectionType sectionType;
    private int sequence;

    public SectionAdapter(Later<SectionGroup> group, SectionType sectionType,
                          int sequence, String shortCode, List<Later<Award>> awards,
                          String name) {
        this.group = group;
        this.sectionType = sectionType;
        this.sequence = sequence;
        this.shortCode = shortCode;
        this.awards = awards;
        this.name = name;
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
        return awards.stream().map(Later::get)
                .filter(isValidAward())
                .collect(UtilityMethods.toLinkedSet());
    }

    @Override
    public String getId() {
        return Section.super.getId();
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public String getShortCode() {
        return shortCode;
    }

    @Override
    public SectionGroup getContainer() {
        return group.get();
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public String getSectionTitle() {
        return name != null ? name : Section.super.getSectionTitle();
    }
}
