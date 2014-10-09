package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Reward;
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
    private List<Later<Reward>> rewards = new ArrayList<>(2);
    private Later<SectionGroup> futureGroup;

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
                rewards);
    }

    SectionBuilder setGroup(Later<SectionGroup> group) {
        this.group = group;
        return this;
    }

    final SectionBuilder addRewards(List<Later<Reward>> moreRewards) {
        if(moreRewards != null) {
            for(Later<Reward> reward: moreRewards) {
                if(reward != null) {
                    this.rewards.add(reward);
                }
            }
        }
        return this;
    }

    void identifyFutureGroup(Later<SectionGroup> futureGroup) {
        if(this.futureGroup == null) { // only attach to the first one
            this.futureGroup = futureGroup;
        }
    }

    Later<SectionGroup> getGroup() {
        return futureGroup;
    }
}
