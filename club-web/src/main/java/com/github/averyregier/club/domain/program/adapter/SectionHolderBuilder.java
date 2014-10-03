package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.SectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by avery on 9/14/14.
 */
public abstract class SectionHolderBuilder<T extends SectionHolderBuilder> {
    protected List<SectionBuilder> sections = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public T section(SectionBuilder sectionBuilder) {
        sections.add(sectionBuilder);
        return (T)this;
    }

    public T section(int sequence, SectionType sectionType, Function<SectionBuilder, SectionBuilder> function) {
        return section(function.apply(new SectionBuilder(sequence, sectionType)));
    }

    public T section(int sequence, SectionType sectionType) {
        return section(new SectionBuilder(sequence, sectionType));
    }
}
