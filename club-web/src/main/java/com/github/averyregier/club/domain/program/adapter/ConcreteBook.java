package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.builder.Later;
import com.github.averyregier.club.domain.program.*;

import java.util.List;
import java.util.stream.Collectors;

/**
* Created by avery on 9/11/2014.
*/
class ConcreteBook implements Book {
    private Later<Curriculum> curriculumLater;
    private final List<SectionGroup> sectionGroups;
    private int sequence;
    private BookVersion bookVersion;
    private String shortCode;

    public ConcreteBook(Later<Curriculum> curriculumLater, int sequence, List<SectionGroup> sectionGroups,
                        BookVersion bookVersion, String shortCode) {
        this.curriculumLater = curriculumLater;
        this.sectionGroups = sectionGroups;
        this.sequence = sequence;
        this.bookVersion = bookVersion;
        this.shortCode = shortCode;
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public List<AgeGroup> getAgeGroups() {
        return null;
    }

    @Override
    public List<SectionGroup> getSectionGroups() {
        return sectionGroups;
    }

    @Override
    public BookVersion getVersion() {
        return bookVersion;
    }

    @Override
    public String getShortCode() {
        return shortCode;
    }

    @Override
    public Curriculum getContainer() {
        return curriculumLater.get();
    }

    @Override
    public List<Section> getSections() {
        return getSectionGroups().stream().flatMap(g -> g.getSections().stream()).collect(Collectors.toList());
    }

    @Override
    public Book getBook() {
        return this;
    }

    @Override
    public String getId() {
        return getContainer().getId()+":"+getShortCode()+getVersion();
    }

}
