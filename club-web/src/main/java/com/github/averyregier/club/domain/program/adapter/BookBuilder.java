package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/7/2014.
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
        Book book = new ConcreteBook(curriculumLater, sequence, sectionGroups,
                new BookVersionAdapter(major, minor, translation, locale, year), shortCode);
        futureBook.set(book);
        completions.forEach(RewardBuilder::build);
        if(reward != null) {
            book.getSections().stream()
                    .filter(s -> s.getSectionType().requiredForBookReward())
                    .forEach(rewardBuilder::section);
            reward.set(rewardBuilder.build());
        }
        return book;
    }

    private List<SectionGroup> buildSectionGroups(Later<Book> futureBook) {
        return sectionGroupBuilders.stream()
                .map(b -> b.setBook(futureBook).build(this))
                .collect(Collectors.toList());
    }

    public BookBuilder group(SectionGroupBuilder sectionGroupBuilder) {
        this.sectionGroupBuilders.add(sectionGroupBuilder);
        return this;
    }

    void addCompletions(ArrayList<RewardBuilder> rewards) {
        completions.addAll(rewards);
    }

    public BookBuilder reward(RewardBuilder rewardBuilder) {
        this.rewardBuilder = rewardBuilder;
        this.rewardBuilder.type(RewardType.book);
        reward = new Later<>();
        return this;
    }

    public BookBuilder reward(Function<RewardBuilder, RewardBuilder> function) {
        return reward(function.apply(new RewardBuilder()));
    }

    public BookBuilder group(int sequence, Function<SectionGroupBuilder, SectionGroupBuilder> setupFn) {
        group(setupFn.apply(new SectionGroupBuilder(sequence)));
        return this;
    }

    Later<Reward> getReward() {
        return reward;
    }

    public BookBuilder version(int major, int minor) {
        this.major = major;
        this.minor = minor;
        return this;
    }

    public BookBuilder shortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public BookBuilder translation(Translation translation) {
        this.translation = translation;
        return this;
    }

    public BookBuilder language(Locale locale) {
        this.locale = locale;
        return this;
    }

    BookBuilder setCurriculum(Later<Curriculum> curriculumLater) {
        this.curriculumLater = curriculumLater;
        return this;
    }

    public BookBuilder reward() {
        return reward(new RewardBuilder());
    }
}
