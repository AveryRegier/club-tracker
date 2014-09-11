package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;

import java.util.Optional;

/**
* Created by rx39789 on 9/10/2014.
*/
class SectionAdapter implements Section {
    private Later<SectionGroup> group;
    private SectionType sectionType;
    private int sequence;

    public SectionAdapter(Later<SectionGroup> group, SectionType sectionType, int sequence) {
        this.group = group;
        this.sectionType = sectionType;
        this.sequence = sequence;
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
    public Optional<SectionGroup> getRewardGroup() {
        return null;
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public String getId() {
        return null;
    }
}
