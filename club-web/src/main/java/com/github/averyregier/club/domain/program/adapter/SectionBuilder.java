package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;

import java.util.Optional;

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
        return new Section() {
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
        };
    }

    public SectionBuilder setGroup(Later<SectionGroup> group) {
        this.group = group;
        return this;
    }
}
