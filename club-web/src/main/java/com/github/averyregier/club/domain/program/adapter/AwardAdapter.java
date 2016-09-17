package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
* Created by avery on 9/11/2014.
*/
class AwardAdapter implements Award {

    private String name;
    private List<Section> builtSections;
    private AccomplishmentLevel accomplishmentLevel;

    public AwardAdapter(String name, List<Section> builtSections, AccomplishmentLevel accomplishmentLevel) {
        this.name = name;
        this.builtSections = builtSections;
        this.accomplishmentLevel = accomplishmentLevel;
    }

    @Override
    public AccomplishmentLevel getAccomplishmentLevel() {
        return accomplishmentLevel;
    }

    @Override
    public Catalogued select(Predicate<Catalogued> filter) {
        return list().get(0);
    }

    @Override
    public List<Catalogued> list() {
        return Arrays.asList((Catalogued) AwardAdapter.this::getName);
    }

    @Override
    public Catalogued select() {
        return select((Predicate<Catalogued>) null);
    }

    @Override
    public String getName() {
        return name != null ?
                name :
                accomplishmentLevel.isBook() ?
                        getBook().getName() :
                        builtSections.get(0).getGroup().getName();
    }

    @Override
    public List<Section> getSections() {
        return builtSections;
    }

    @Override
    public Book getBook() {
        return builtSections.get(0).getGroup().getBook();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getDisplayName() {
        return accomplishmentLevel.isBook() ? getName() : getBook().getName() + " " + getName();
    }
}
