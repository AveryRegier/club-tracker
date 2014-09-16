package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Reward;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avery on 9/7/2014.
 */
public class SectionBuilder {
    private int sequence;
    private SectionType sectionType;
    private Later<SectionGroup> group;
    private List<Later<Reward>> rewards = new ArrayList<>(2);
    private Later<SectionGroup> futureGroup;

    public SectionBuilder(int sequence, SectionType sectionType) {
        this.sequence = sequence;
        this.sectionType = sectionType;
    }

    public Section build() {
        return new SectionAdapter(group, sectionType, sequence, rewards);
    }

    SectionBuilder setGroup(Later<SectionGroup> group) {
        this.group = group;
        return this;
    }

    SectionBuilder addRewards(Later<Reward>... moreRewards) {
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
