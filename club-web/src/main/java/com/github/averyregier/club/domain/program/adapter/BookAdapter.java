package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by rx39789 on 9/7/2014.
 */
public abstract class BookAdapter implements Book {
    @Override
    public List<SectionGroup> getSectionGroups() {
        return Collections.emptyList();
    }

    @Override
    public List<AgeGroup> getAgeGroups() {
        return null;
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
        return null;
    }

    @Override
    public BookVersion getVersion() {
        return null;
    }

    @Override
    public Curriculum getContainer() {
        return null;
    }

    @Override
    public String getShortCode() {
        return null;
    }
}
