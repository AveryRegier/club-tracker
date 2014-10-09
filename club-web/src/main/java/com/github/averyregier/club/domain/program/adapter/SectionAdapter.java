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
    private final List<Later<Reward>> rewards;
    private Later<SectionGroup> group;
    private SectionType sectionType;
    private int sequence;

    public SectionAdapter(Later<SectionGroup> group, SectionType sectionType,
                          int sequence, String shortCode, List<Later<Reward>> rewards) {
        this.group = group;
        this.sectionType = sectionType;
        this.sequence = sequence;
        this.shortCode = shortCode;
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
        return rewards.stream().map(r->r.get())
                .filter(isValidReward())
                .collect(UtilityMethods.toLinkedSet());
    }

    @Override
    public Set<Reward> getRewards(RewardType type) {
        return getRewards().stream()
                .filter(t->t.getRewardType() == type)
                .filter(isValidReward())
                .collect(UtilityMethods.toLinkedSet());
    }

    private Predicate<Reward> isValidReward() {
        return t->t.getRewardType() == RewardType.book &&
                   getSectionType().requiredForBookReward() ||
                   t.getRewardType() != RewardType.book;
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
