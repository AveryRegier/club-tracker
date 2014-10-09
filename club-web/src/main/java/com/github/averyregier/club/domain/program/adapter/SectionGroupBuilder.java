package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/7/2014.
 */
public class SectionGroupBuilder extends SectionHolderBuilder<SectionGroupBuilder> implements Builder<SectionGroup> {
    private ArrayList<RewardBuilder> rewards = new ArrayList<>();
    private int sequence;
    private Later<Book> futureBook;
    private TreeSet<Section> allSections = new TreeSet<>();
    private Later<SectionGroup> futureGroup = new Later<>();

    public SectionGroupBuilder(int sequence) {
        this.sequence = sequence;
    }

    public SectionGroup build() {
       return build(null);
    }

    SectionGroup build(BookBuilder bookBuilder) {
        List<Later<Reward>> bookReward = bookBuilder != null ? bookBuilder.getReward() : Collections.emptyList();

        rewards.forEach(reward->allSections.addAll(reward.build(futureGroup, bookReward)));
        final List<Section> sections = buildSections(futureGroup, bookReward);
        SectionGroupAdapter group = new SectionGroupAdapter(
                futureBook, sequence, name, sections);
        futureGroup.set(group);
        if(!rewards.isEmpty() && bookBuilder != null) {
            bookBuilder.addCompletions(rewards);
        }
        return group;
    }

    @SuppressWarnings("unchecked")
    private List<Section> buildSections(Later<SectionGroup> futureGroup, List<Later<Reward>> bookReward) {

        applyDecider();
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
        if(decider != null) reward.typeAssigner(decider);
        rewards.add(reward);
        return this;
    }

    public SectionGroupBuilder reward(UnaryOperator<RewardBuilder> function) {
        return reward(function.apply(new RewardBuilder()));
    }

}
