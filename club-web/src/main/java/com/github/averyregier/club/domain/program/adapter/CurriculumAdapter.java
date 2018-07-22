package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Created by avery on 9/15/14.
*/
class CurriculumAdapter implements Curriculum {
    private String name;
    private final List<Book> bookList;
    private String shortCode;
    private Later<Curriculum> parentCurriculum;
    private boolean scheduled;
    private Function<AgeGroup, Boolean> acceptsFn;

    public CurriculumAdapter(String shortCode, String name, List<Book> bookList, Later<Curriculum> parentCurriculum,
                             boolean scheduled, Function<AgeGroup, Boolean> acceptsFn)
    {
        this.name = name;
        this.bookList = bookList;
        this.shortCode = shortCode;
        this.parentCurriculum = parentCurriculum;
        this.scheduled = scheduled;
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
        String[] parts = clubId.split(":");
        if(parts[0].equals(getShortCode())) {
            String[] temp = new String[parts.length - 1];
            System.arraycopy(parts, 1, temp, 0,parts.length-1);
            parts = temp;
        }
        Optional<Curriculum> current = Optional.of(this);
        for(String part: parts) {
            current = current.flatMap(c->c.getSeries().stream()
                    .filter(s -> s.getShortCode().equals(part))
                    .findFirst());
        }
        return current.orElse(null) != this ? current : Optional.empty();
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

    @Override
    public Optional<Book> lookupBook(String bookId) {
        return
                getBooks().stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst();
    }

    @Override
    public Collection<AgeGroup> getAgeGroups() {
        return Curriculum.super.getAgeGroups();
    }

    @Override
    public List<Curriculum> getAllSeries() {
        return Curriculum.super.getAllSeries();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isScheduled() {
        if(!scheduled && parentCurriculum != null && parentCurriculum.isPresent()) {
            return parentCurriculum.get().isScheduled();
        }
        return scheduled;
    }
}
