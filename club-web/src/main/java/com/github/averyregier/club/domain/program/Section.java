package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Contained;

import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface Section extends Comparable<Section>, Contained<SectionGroup> {
    public SectionType getSectionType();
    public SectionGroup getGroup();

    public Set<Reward> getRewards();
    public Set<Reward> getRewards(RewardType type);

    int sequence();

    String getId();
}
