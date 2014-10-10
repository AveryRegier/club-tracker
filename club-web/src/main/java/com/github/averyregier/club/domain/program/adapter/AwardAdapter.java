package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Section;

import java.util.List;

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
}
