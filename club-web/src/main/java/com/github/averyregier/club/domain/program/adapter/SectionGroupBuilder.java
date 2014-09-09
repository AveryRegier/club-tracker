package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rx39789 on 9/7/2014.
 */
public class SectionGroupBuilder {
    private ArrayList<SectionBuilder> sections = new ArrayList<>();
    private int sequence;

    public SectionGroupBuilder(int sequence) {
        this.sequence = sequence;
    }

    public SectionGroup build() {
        final List<Section> sections = buildSections();
        return new SectionGroupAdapter(sequence, sections);
    }

    private List<Section> buildSections() {
        return sections.stream().map(b->b.build()).collect(Collectors.toList());
    }

    public SectionGroupBuilder addSection(SectionBuilder sectionBuilder) {
        sections.add(sectionBuilder);
        return this;
    }

}
