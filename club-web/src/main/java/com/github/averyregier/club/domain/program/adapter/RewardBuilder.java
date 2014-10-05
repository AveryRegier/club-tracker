package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Reward;
import com.github.averyregier.club.domain.program.RewardType;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.utility.builder.Later;
import com.sun.istack.internal.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/11/2014.
 */
public class RewardBuilder extends SectionHolderBuilder<RewardBuilder> implements Builder<Reward> {
    private Later<Reward> futureReward = new Later<>();
    private List<Section> builtSections = new ArrayList<>();
    private RewardType rewardType;

    @SuppressWarnings("unchecked")
    List<Section> build(Later<SectionGroup> futureGroup, Later<Reward> bookReward ) {
        List<Section> currentSections = buildSections(futureGroup, futureReward, bookReward);
        builtSections.addAll(currentSections);
        return currentSections;
    }

    public Reward build() {
        if(!sections.isEmpty()) {
            throw new IllegalStateException();
        }
        Reward reward = new RewardAdapter(name, builtSections, rewardType);
        futureReward.set(reward);
        return reward;
    }

    @SuppressWarnings("unchecked")
    private synchronized List<Section> buildSections(Later<SectionGroup> futureGroup, Later<Reward>... futureReward) {
        List<SectionBuilder> relevant = sections.stream()
                .filter(s -> s.getGroup() == futureGroup)
                .collect(Collectors.toList());
        List<Section> collect = relevant.stream()
                .map(b -> b.setGroup(futureGroup)
                           .addRewards(futureReward)
                           .build())
                .collect(Collectors.toList());
        sections.removeAll(relevant);
        return collect;
    }

    public void section(Section s) {
        builtSections.add(s);
    }

    public void type(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    void identifySectionGroup(Later<SectionGroup> futureGroup) {
        sections.forEach(s->s.identifyFutureGroup(futureGroup));
    }
}
