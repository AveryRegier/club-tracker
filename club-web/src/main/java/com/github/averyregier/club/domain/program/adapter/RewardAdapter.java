package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Reward;
import com.github.averyregier.club.domain.program.RewardType;
import com.github.averyregier.club.domain.program.Section;

import java.util.List;

/**
* Created by rx39789 on 9/11/2014.
*/
class RewardAdapter implements Reward {

    private List<Section> builtSections;
    private RewardType rewardType;

    public RewardAdapter(List<Section> builtSections, RewardType rewardType) {
        this.builtSections = builtSections;
        this.rewardType = rewardType;
    }

    @Override
    public RewardType getRewardType() {
        return rewardType;
    }

    @Override
    public List<Section> getSections() {
        return builtSections;
    }
}
