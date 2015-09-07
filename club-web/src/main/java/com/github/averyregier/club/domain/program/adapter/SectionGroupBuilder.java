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
    private ArrayList<AwardBuilder> awards = new ArrayList<>();
    private int sequence;
    private Later<Book> futureBook;
    private TreeSet<Section> allSections = new TreeSet<>();
    private Later<SectionGroup> futureGroup = new Later<>();
    private String shortCode;

    public SectionGroupBuilder(int sequence) {
        this.sequence = sequence;
        this.shortCode = Integer.toString(sequence);
    }

    public SectionGroup build() {
       return build(null);
    }

    SectionGroup build(BookBuilder bookBuilder) {
        List<Later<Award>> bookReward = bookBuilder != null ? bookBuilder.getAwards() : Collections.emptyList();

        awards.forEach(reward -> allSections.addAll(reward.build(futureGroup, bookReward)));
        final List<Section> sections = buildSections(futureGroup, bookReward);
        SectionGroupAdapter group = new SectionGroupAdapter(shortCode,
                futureBook, sequence, name, sections);
        futureGroup.set(group);
        if(!awards.isEmpty() && bookBuilder != null) {
            bookBuilder.addCompletions(awards);
        }
        return group;
    }

    @SuppressWarnings("unchecked")
    private List<Section> buildSections(Later<SectionGroup> futureGroup, List<Later<Award>> bookReward) {

        applyDecider();
        allSections.addAll(sections.stream()
                .map(b -> b.setGroup(futureGroup).addAwards(bookReward).build())
                .collect(Collectors.toList()));
        return new ArrayList<>(allSections);
    }

    SectionGroupBuilder setBook(Later<Book> book) {
        this.futureBook = book;
        return this;
    }

    public SectionGroupBuilder award(AwardBuilder award) {
        award.type(AccomplishmentLevel.group);
        award.identifySectionGroup(futureGroup);
        if(decider != null) award.typeAssigner(decider);
        awards.add(award);
        return this;
    }

    public SectionGroupBuilder award(UnaryOperator<AwardBuilder> function) {
        return award(function.apply(new AwardBuilder()));
    }

    public SectionGroupBuilder shortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }
}
