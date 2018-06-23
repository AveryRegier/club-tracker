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
    protected String name;
    protected Function<Integer, SectionType> decider;

    private T section(SectionBuilder sectionBuilder) {
        sections.add(sectionBuilder);
        return self();
    }

    public SectionBuilder addSection(SectionBuilder sectionBuilder) {
        section(sectionBuilder);
        return sectionBuilder;
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T)this;
    }

    public T section(int sequence, SectionType sectionType, Function<SectionBuilder, SectionBuilder> function) {
        return section(function.apply(new SectionBuilder(sequence, sectionType)));
    }

    public T section(int sequence, Function<SectionBuilder, SectionBuilder> function) {
        return section(function.apply(new SectionBuilder(sequence)));
    }

    public T section(int sequence, SectionType sectionType) {
        return section(new SectionBuilder(sequence, sectionType));
    }

    public T section(int sequence) {
        return section(new SectionBuilder(sequence));
    }

    public T name(String name) {
        this.name = name;
        return self();
    }

    protected void applyDecider() {
        if(decider != null)
            sections.stream().forEach(s-> s.type(decider.apply(s.getSequence())));
    }

    T typeAssigner(Function<Integer, SectionType> decider) {
        this.decider = decider;
        return self();
    }
}
