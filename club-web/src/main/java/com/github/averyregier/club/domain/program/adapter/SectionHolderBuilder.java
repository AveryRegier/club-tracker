package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.SectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by avery on 9/14/14.
 */
public class SectionHolderBuilder<T extends SectionHolderBuilder> {
    protected List<SectionBuilder> sections = new ArrayList<>();

    public T addSection(SectionBuilder sectionBuilder) {
        sections.add(sectionBuilder);
        return (T)this;
    }

    public T addSection(int sequence, SectionType sectionType, Function<SectionBuilder, SectionBuilder> function) {
        return addSection(function.apply(new SectionBuilder(sequence, sectionType)));
    }

    public T addSection(int sequence, SectionType sectionType) {
        return addSection(new SectionBuilder(sequence, sectionType));
    }
}
