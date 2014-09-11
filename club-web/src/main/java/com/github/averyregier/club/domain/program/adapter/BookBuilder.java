package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by rx39789 on 9/7/2014.
 */
public class BookBuilder {
    private List<SectionGroupBuilder> sectionGroupBuilders = new ArrayList<>();
    private int sequence;
    private Set<RewardBuilder> completions = new HashSet<>();
    private Later<Reward> reward;
    private RewardBuilder rewardBuilder;

    public BookBuilder(int sequence) {
        this.sequence = sequence;
    }

    public Book build() {
        Later<Book> futureBook = new Later<>();
        List<SectionGroup> sectionGroups = buildSectionGroups(futureBook);
        BookAdapter book = new ConcreteBook(sequence, sectionGroups);
        futureBook.set(book);
        completions.forEach(RewardBuilder::build);
        if(reward != null) {
            book.getSections().stream()
                    .filter(s -> s.getSectionType().requiredForBookReward())
                    .forEach(rewardBuilder::addSection);
            reward.set(rewardBuilder.build());
        }
        return book;
    }

    private List<SectionGroup> buildSectionGroups(Later<Book> futureBook) {
        return sectionGroupBuilders.stream()
                .map(b -> b.setBook(futureBook).build(this))
                .collect(Collectors.toList());
    }

    public BookBuilder addSectionGroup(SectionGroupBuilder sectionGroupBuilder) {
        this.sectionGroupBuilders.add(sectionGroupBuilder);
        return this;
    }

    public void addCompletions(ArrayList<RewardBuilder> rewards) {
        completions.addAll(rewards);
    }

    public BookBuilder addReward(RewardBuilder rewardBuilder) {
        this.rewardBuilder = rewardBuilder;
        this.rewardBuilder.setRewardType(RewardType.book);
        reward = new Later<>();
        return this;
    }

    public Later<Reward> getReward() {
        return reward;
    }

}
