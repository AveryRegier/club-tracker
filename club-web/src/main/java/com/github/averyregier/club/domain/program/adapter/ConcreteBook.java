package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.BookVersion;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.SectionGroup;

import java.util.List;

/**
* Created by avery on 9/11/2014.
*/
class ConcreteBook extends BookAdapter {
    private Later<Curriculum> curriculumLater;
    private final List<SectionGroup> sectionGroups;
    private int sequence;
    private BookVersion bookVersion;
    private String shortCode;

    public ConcreteBook(Later<Curriculum> curriculumLater, int sequence, List<SectionGroup> sectionGroups, BookVersion bookVersion, String shortCode) {
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
}
