package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Reward;
import com.github.averyregier.club.domain.program.RewardType;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rx39789 on 9/11/2014.
 */
public class RewardBuilder {
    private List<SectionBuilder> sections = new ArrayList<>();
    private Later<Reward> futureReward = new Later<>();
    private List<Section> builtSections = new ArrayList<>();
    private RewardType rewardType;

    public RewardBuilder addSection(SectionBuilder sectionBuilder) {
        sections.add(sectionBuilder);
        return this;
    }

    public List<Section> build(Later<SectionGroup> futureGroup, Later<Reward> bookReward ) {
        List<Section> currentSections = buildSections(futureGroup, futureReward, bookReward);
        builtSections.addAll(currentSections);
        return currentSections;
    }

    public Reward build() {
        if(!sections.isEmpty()) {
            throw new IllegalStateException();
        }
        Reward reward = new RewardAdapter(builtSections, rewardType);
        futureReward.set(reward);
        return reward;
    }

    private synchronized List<Section> buildSections(Later<SectionGroup> futureGroup, Later<Reward>... futureReward) {
        List<SectionBuilder> relevant = sections.stream()
                .filter(s -> s.getGroup() == futureGroup).collect(Collectors.toList());
        List<Section> collect = relevant.stream()
                .map(b -> {
                    return b.setGroup(futureGroup)
                            .addRewards(futureReward)
                            .build();
                })
                .collect(Collectors.toList());
        sections.removeAll(relevant);
        return collect;
    }

    public void addSection(Section s) {
        builtSections.add(s);
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public void identifySectionGroup(Later<SectionGroup> futureGroup) {
        sections.forEach(s->s.identifyFutureGroup(futureGroup));
    }
}
