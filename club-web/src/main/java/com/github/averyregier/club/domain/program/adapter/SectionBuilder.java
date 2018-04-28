package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avery on 9/7/2014.
 */
public class SectionBuilder implements Builder<Section> {
    private int sequence;
    private String shortCode;
    private SectionType sectionType;
    private Later<SectionGroup> group;
    private List<Later<Award>> awards = new ArrayList<>(2);
    private Later<SectionGroup> futureGroup;
    private String name;

    public SectionBuilder(int sequence, SectionType sectionType) {
        this.sequence = sequence;
        this.sectionType = sectionType;
    }

    SectionBuilder(int sequence) {
        this.sequence = sequence;
    }

    public SectionBuilder type(SectionType type) {
        this.sectionType = type;
        return this;
    }

    public int getSequence() {
        return sequence;
    }

    public SectionBuilder shortCode(String code) {
        this.shortCode = code;
        return this;
    }

    public Section build() {
        return new SectionAdapter(
                group,
                sectionType,
                sequence,
                shortCode != null ? shortCode : Integer.toString(sequence),
                awards,
                name);
    }

    SectionBuilder setGroup(Later<SectionGroup> group) {
        this.group = group;
        return this;
    }

    final SectionBuilder addAwards(List<Later<Award>> moreAwards) {
        if(moreAwards != null) {
            for(Later<Award> award: moreAwards) {
                if(award != null) {
                    this.awards.add(award);
                }
            }
        }
        return this;
    }

    void identifyFutureGroup(Later<SectionGroup> futureGroup) {
        if(this.futureGroup == null) { // only attach to the given one
            this.futureGroup = futureGroup;
        }
    }

    Later<SectionGroup> getGroup() {
        return futureGroup;
    }

    public SectionBuilder name(String name) {
        this.name = name;
        return this;
    }
}
