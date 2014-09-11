package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by rx39789 on 9/7/2014.
 */
public class SectionGroupBuilder {
    private ArrayList<SectionBuilder> sections = new ArrayList<>();
    private ArrayList<RewardBuilder> rewards = new ArrayList<>();
    private int sequence;
    private Later<Book> futureBook;
    TreeSet<Section> allSections = new TreeSet<>();
    Later<SectionGroup> futureGroup = new Later<>();

    public SectionGroupBuilder(int sequence) {
        this.sequence = sequence;
    }

    public SectionGroup build() {
       return build(null);
    }

    public SectionGroup build(BookBuilder bookBuilder) {
        Later<Reward> bookReward = bookBuilder != null ? bookBuilder.getReward() : null;

        rewards.forEach(reward->allSections.addAll(reward.build(futureGroup, bookReward)));
        final List<Section> sections = buildSections(futureGroup, bookReward);
        SectionGroupAdapter group = new SectionGroupAdapter(futureBook, sequence, sections);
        futureGroup.set(group);
        if(!rewards.isEmpty()) {
            bookBuilder.addCompletions(rewards);
        }
        return group;
    }

    private List<Section> buildSections(Later<SectionGroup> futureGroup, Later<Reward> bookReward) {

        allSections.addAll(sections.stream()
                .map(b -> b.setGroup(futureGroup).addRewards(bookReward).build())
                .collect(Collectors.toList()));
        return new ArrayList<>(allSections);
    }

    public SectionGroupBuilder addSection(SectionBuilder sectionBuilder) {
        sections.add(sectionBuilder);
        return this;
    }

    public SectionGroupBuilder setBook(Later<Book> book) {
        this.futureBook = book;
        return this;
    }

    public SectionGroupBuilder addReward(RewardBuilder reward) {
        reward.setRewardType(RewardType.group);
        reward.identifySectionGroup(futureGroup);
        rewards.add(reward);
        return this;
    }
}
