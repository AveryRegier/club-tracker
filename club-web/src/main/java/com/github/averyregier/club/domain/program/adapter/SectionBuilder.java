package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;

/**
 * Created by rx39789 on 9/7/2014.
 */
public class SectionBuilder {
    private int sequence;
    private SectionType sectionType;
    private Later<SectionGroup> group;

    public SectionBuilder(int sequence, SectionType sectionType) {
        this.sequence = sequence;
        this.sectionType = sectionType;
    }

    public Section build() {
        return new SectionAdapter(group, sectionType, sequence);
    }

    public SectionBuilder setGroup(Later<SectionGroup> group) {
        this.group = group;
        return this;
    }

}
