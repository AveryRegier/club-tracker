package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.time.Year;
import java.util.*;
import java.util.function.Function;
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
    private String shortCode;
    private Translation translation = Translation.none;
    private Locale locale = Locale.ENGLISH;
    private int major = 0;
    private int minor = 0;
    private Year year = Year.now();
    private Later<Curriculum> curriculumLater;

    public BookBuilder(int sequence) {
        this.sequence = sequence;
    }

    public Book build() {
        Later<Book> futureBook = new Later<>();
        List<SectionGroup> sectionGroups = buildSectionGroups(futureBook);
        BookAdapter book = new ConcreteBook(curriculumLater, sequence, sectionGroups,
                new BookVersionAdapter(major, minor, translation, locale, year), shortCode);
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

    public BookBuilder addReward(Function<RewardBuilder, RewardBuilder> function) {
        return addReward(function.apply(new RewardBuilder()));
    }

    public BookBuilder addSectionGroup(int sequence, Function<SectionGroupBuilder, SectionGroupBuilder> setupFn) {
        addSectionGroup(setupFn.apply(new SectionGroupBuilder(sequence)));
        return this;
    }

    public Later<Reward> getReward() {
        return reward;
    }

    public BookBuilder setVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
        return this;
    }

    public BookBuilder setShortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public BookBuilder setTranslation(Translation translation) {
        this.translation = translation;
        return this;
    }

    public BookBuilder setLanguage(Locale locale) {
        this.locale = locale;
        return this;
    }

    public BookBuilder setCurriculum(Later<Curriculum> curriculumLater) {
        this.curriculumLater = curriculumLater;
        return this;
    }
}
