package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.List;
import java.util.stream.Collectors;

/**
* Created by avery on 9/11/2014.
*/
class ConcreteBook implements Book {
    private Later<Curriculum> curriculumLater;
    private String name;
    private String mwhCode;
    private final List<SectionGroup> sectionGroups;
    private int sequence;
    private BookVersion bookVersion;
    private String shortCode;
    private List<AgeGroup> ageGroups;

    public ConcreteBook(Later<Curriculum> curriculumLater, int sequence, String name, String mwhCode, List<SectionGroup> sectionGroups,
                        BookVersion bookVersion, String shortCode, List<AgeGroup> ageGroups) {
        this.curriculumLater = curriculumLater;
        this.name = name;
        this.mwhCode = mwhCode;
        this.sectionGroups = sectionGroups;
        this.sequence = sequence;
        this.bookVersion = bookVersion;
        this.shortCode = shortCode;
        this.ageGroups = ageGroups;
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public List<AgeGroup> getAgeGroups() {
        return ageGroups;
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
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return getContainer().getId()+":"+getShortCode()+getVersion();
    }

    @Override
    public int compareTo(Book o) {
        return o == this ? 0 :  o.getContainer().getBooks().indexOf(o) - o.getContainer().getBooks().indexOf(this);
    }

    public String getMwhCode() {
        return mwhCode;
    }
}
