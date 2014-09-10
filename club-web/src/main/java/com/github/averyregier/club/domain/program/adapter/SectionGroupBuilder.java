package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by rx39789 on 9/7/2014.
 */
public class SectionGroupBuilder {
    private ArrayList<SectionBuilder> sections = new ArrayList<>();
    private int sequence;
    private Later<Book> futureBook;

    public SectionGroupBuilder(int sequence) {
        this.sequence = sequence;
    }

    public SectionGroup build() {
        Later<SectionGroup> futureGroup = new Later<>();
        final List<Section> sections = buildSections(futureGroup);
        SectionGroupAdapter group = new SectionGroupAdapter(futureBook, sequence, sections);
        futureGroup.set(group);
        return group;
    }

    private List<Section> buildSections(Later<SectionGroup> futureGroup) {
        return sections.stream().map(b->b.setGroup(futureGroup).build()).collect(Collectors.toList());
    }

    public SectionGroupBuilder addSection(SectionBuilder sectionBuilder) {
        sections.add(sectionBuilder);
        return this;
    }

    public SectionGroupBuilder setBook(Later<Book> book) {
        this.futureBook = book;
        return this;
    }
}
