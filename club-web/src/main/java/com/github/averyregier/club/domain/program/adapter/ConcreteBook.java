package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.SectionGroup;

import java.util.List;

/**
* Created by rx39789 on 9/11/2014.
*/
class ConcreteBook extends BookAdapter {
    private final List<SectionGroup> sectionGroups;
    private int sequence;

    public ConcreteBook(int sequence, List<SectionGroup> sectionGroups) {
        this.sectionGroups = sectionGroups;
        this.sequence = sequence;
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public List<SectionGroup> getSectionGroups() {
        return sectionGroups;
    }
}
