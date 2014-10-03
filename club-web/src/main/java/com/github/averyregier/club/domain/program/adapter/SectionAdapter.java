package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* Created by avery on 9/10/2014.
*/
class SectionAdapter implements Section {
    private final List<Later<Reward>> rewards;
    private Later<SectionGroup> group;
    private SectionType sectionType;
    private int sequence;

    public SectionAdapter(Later<SectionGroup> group, SectionType sectionType, int sequence, List<Later<Reward>> rewards) {
        this.group = group;
        this.sectionType = sectionType;
        this.sequence = sequence;
        this.rewards = rewards;
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
    public Set<Reward> getRewards() {
        return rewards.stream().map(r->r.get()).collect(Collectors.toSet());
    }

    @Override
    public Set<Reward> getRewards(RewardType group) {
        return getRewards().stream()
                .filter(t->t.getRewardType() == group)
                .collect(Collectors.toSet());
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
        return Integer.toString(sequence());
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
