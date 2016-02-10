package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.time.Year;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/7/2014.
 */
public class BookBuilder implements Builder<Book> {
    private List<SectionGroupBuilder> sectionGroupBuilders = new ArrayList<>();
    private int sequence;
    private Set<AwardBuilder> completions = new HashSet<>();
    private List<Later<Award>> awards = new ArrayList<>(2);
    private List<AwardBuilder> awardBuilders = new ArrayList<>(2);
    private String shortCode;
    private Translation translation = Translation.none;
    private Locale locale = Locale.ENGLISH;
    private int major = 0;
    private int minor = 0;
    private Year year = Year.now();
    private Later<Curriculum> curriculumLater;
    private List<AgeGroup> ageGroups = new ArrayList<>();
    private String name;
    private SectionTypeDecider decider;
    private String mwhCode;

    public BookBuilder(int sequence) {
        this.sequence = sequence;
    }

    public Book build() {
        Later<Book> futureBook = new Later<>();
        List<SectionGroup> sectionGroups = buildSectionGroups(futureBook);
        Book book = new ConcreteBook(
                curriculumLater,
                sequence,
                name != null ? name : Integer.toString(sequence),
                mwhCode != null ? mwhCode : Integer.toString(sequence),
                sectionGroups,
                new BookVersionAdapter(major, minor, translation, locale, year),
                shortCode,
                ageGroups);
        futureBook.set(book);
        completions.forEach(AwardBuilder::build);
        if(!awards.isEmpty()) {
            for(int i=0; i< awards.size(); i++) {
                book.getSections().stream()
                        .filter(s -> s.getSectionType().requiredFor(AccomplishmentLevel.book))
                        .forEach(awardBuilders.get(i)::section);
                awards.get(i).set(awardBuilders.get(i).build());
            }
        }
        return book;
    }

    private List<SectionGroup> buildSectionGroups(Later<Book> futureBook) {
        return sectionGroupBuilders.stream()
                .map(b -> b.setBook(futureBook).build(this))
                .collect(Collectors.toList());
    }

    private BookBuilder group(SectionGroupBuilder sectionGroupBuilder) {
        this.sectionGroupBuilders.add(sectionGroupBuilder);
        return this;
    }

    void addCompletions(ArrayList<AwardBuilder> rewards) {
        completions.addAll(rewards);
    }

    public BookBuilder award(AwardBuilder awardBuilder) {
        this.awardBuilders.add(awardBuilder);
        awardBuilder.type(AccomplishmentLevel.book);
        awards.add(new Later<>());
        return this;
    }

    public BookBuilder award(UnaryOperator<AwardBuilder> function) {
        return award(function.apply(new AwardBuilder()));
    }

    public BookBuilder group(int sequence, UnaryOperator<SectionGroupBuilder> setupFn) {
        SectionGroupBuilder groupBuilder = new SectionGroupBuilder(sequence);
        if(decider != null)
            groupBuilder.typeAssigner(s -> decider.decide(sequence, s));
        group(setupFn.apply(groupBuilder));
        return this;
    }

    List<Later<Award>> getAwards() {
        return awards;
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

    public BookBuilder award() {
        return award(new AwardBuilder());
    }

    public BookBuilder ageGroup(AgeGroup ageGroup) {
        ageGroups.add(ageGroup);
        return this;
    }

    public BookBuilder catalog(String reference) {
        return this;
    }

    public BookBuilder catalog(String reference, String quantity) {
        return this;
    }

    public BookBuilder publicationYear(int year) {
        this.year = Year.of(year);
        return this;
    }

    public BookBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BookBuilder typeAssigner(SectionTypeDecider decider) {
        this.decider = decider;
        return this;
    }

    public BookBuilder mwhCode(String mwhCode) {
        this.mwhCode = mwhCode;
        return this;
    }
}
