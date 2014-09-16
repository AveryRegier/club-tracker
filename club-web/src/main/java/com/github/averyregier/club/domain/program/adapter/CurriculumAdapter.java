package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
* Created by avery on 9/15/14.
*/
class CurriculumAdapter implements Curriculum {
    private final List<Book> bookList;
    private String shortCode;

    public CurriculumAdapter(String shortCode, List<Book> bookList) {
        this.bookList = bookList;
        this.shortCode = shortCode;
    }

    @Override
    public List<Book> getBooks() {
        return bookList;
    }

    @Override
    public List<AgeGroup> getAgeGroups() {
        return null;
    }

    @Override
    public Set<SectionType> getSectionTypes() {
        return null;
    }

    @Override
    public Optional<Section> lookup(String sectionId) {
        return null;
    }

    @Override
    public List<Book> recommendedBookList(AgeGroup age) {
        return getBooks();
    }

    @Override
    public List<Translation> getSupportedTranslations(Locale locale) {
        return null;
    }

    @Override
    public String getId() {
        return shortCode;
    }

    @Override
    public String getShortCode() {
        return shortCode;
    }
}
