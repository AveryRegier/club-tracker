package com.github.averyregier.club.domain.program;

import java.util.Set;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Section {
    public SectionType getSectionType();
    public SectionGroup getGroup();

    public Set<Reward> getRewards();
    public Set<Reward> getRewards(RewardType group);

    int sequence();

    String getId();
}
