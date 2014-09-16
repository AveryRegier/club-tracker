package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/7/2014.
 */
public class SectionGroupBuilder extends SectionHolderBuilder<SectionGroupBuilder> {
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

    SectionGroup build(BookBuilder bookBuilder) {
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

    SectionGroupBuilder setBook(Later<Book> book) {
        this.futureBook = book;
        return this;
    }

    public SectionGroupBuilder reward(RewardBuilder reward) {
        reward.type(RewardType.group);
        reward.identifySectionGroup(futureGroup);
        rewards.add(reward);
        return this;
    }

    public SectionGroupBuilder reward(Function<RewardBuilder, RewardBuilder> function) {
        return reward(function.apply(new RewardBuilder()));
    }
}
