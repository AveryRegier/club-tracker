package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Created by avery on 9/15/14.
*/
class CurriculumAdapter implements Curriculum {
    private final List<Book> bookList;
    private String shortCode;
    private Later<Curriculum> parentCurriculum;
    private Function<AgeGroup, Boolean> acceptsFn;

    public CurriculumAdapter(String shortCode, List<Book> bookList, Later<Curriculum> parentCurriculum, Function<AgeGroup, Boolean> acceptsFn) {
        this.bookList = bookList;
        this.shortCode = shortCode;
        this.parentCurriculum = parentCurriculum;
        this.acceptsFn = acceptsFn;
    }

    @Override
    public List<Book> getBooks() {
        return bookList;
    }

    @Override
    public Optional<Section> lookup(String sectionId) {
        if(sectionId != null) {
            String[] split = sectionId.trim().split(":");
            if (split.length == 4 && split[0].equals(shortCode)) {
                for (Book book : bookList) {
                    if (book.getId().endsWith(split[0] + ":" + split[1])) {
                        for (SectionGroup group : book.getSectionGroups()) {
                            if (group.getShortCode().equals(split[2])) {
                                for (Section section : group.getSections()) {
                                    if (section.getShortCode().equals(split[3])) {
                                        return Optional.of(section);
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            } else if(parentCurriculum != null && split.length > 0 && split[0].startsWith(parentCurriculum.get().getId())) {
                return parentCurriculum.get().lookup(sectionId);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Book> recommendedBookList(AgeGroup age) {
        return getBooks().stream()
                .filter(b->b.getAgeGroups().contains(age))
                .collect(Collectors.toList());
    }

    @Override
    public String getId() {
        if(parentCurriculum != null) {
            return parentCurriculum.get().getId()+":"+shortCode;
        }
        return shortCode;
    }

    @Override
    public List<Curriculum> getSeries() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Curriculum> getSeries(String clubId) {
        return getSeries().stream()
                .filter(s->s.getId().equals(clubId))
                .findFirst();
    }

    @Override
    public String getShortCode() {
        return shortCode;
    }

    @Override
    public Curriculum getContainer() {
        return parentCurriculum != null ? parentCurriculum.get() : null;
    }

    @Override
    public boolean accepts(AgeGroup ageGroup) {
        return (acceptsFn != null ? acceptsFn : defaultAcceptsFn()).apply(ageGroup);
    }

    private Function<AgeGroup, Boolean> defaultAcceptsFn() {
        return ageGroup -> !recommendedBookList(ageGroup).isEmpty();
    }
}
