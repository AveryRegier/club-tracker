package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/15/14.
 */
public class CurriculumBuilder implements Builder<Curriculum> {
    private String shortCode;
    private List<BookBuilder> books = new ArrayList<>();
    private List<CurriculumBuilder> series = new ArrayList<>();
    private Later<Curriculum> parentCurriculum;
    private Function<AgeGroup, Boolean> acceptsFn;
    private String name;

    public Curriculum build() {
        Later<Curriculum> curriculumLater = new Later<>();
        CurriculumAdapter curriculum;
        if(!series.isEmpty()) {
            List<Curriculum> series = this.series.stream()
                    .map(s->s.setCurriculum(curriculumLater).build())
                    .collect(Collectors.toList());
            curriculum = new MasterCurriculum(shortCode, getName(), series, parentCurriculum);
        } else {
            List<Book> bookList = buildBooks(curriculumLater);
            curriculum = new CurriculumAdapter(shortCode, getName(), bookList, parentCurriculum, acceptsFn);
        }
        curriculumLater.set(curriculum);
        return curriculum;
    }

    public String getName() {
        return Optional.ofNullable(name).orElse(shortCode);
    }

    private CurriculumBuilder setCurriculum(Later<Curriculum> curriculumLater) {
        parentCurriculum = curriculumLater;
        return this;
    }

    private List<Book> buildBooks(Later<Curriculum> curriculumLater) {
        return books.stream().map(b -> b.setCurriculum(curriculumLater).build()).collect(Collectors.toList());
    }

    public CurriculumBuilder shortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public CurriculumBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CurriculumBuilder book(int sequence, UnaryOperator<BookBuilder> function) {

        books.add(function.apply(new BookBuilder(sequence)));
        return this;
    }

    public CurriculumBuilder curriculum(UnaryOperator<CurriculumBuilder> f) {
        series.add(f.apply(new CurriculumBuilder()));
        return this;
    }

    public CurriculumBuilder accepts(AgeGroup... ageGroups) {
        List<AgeGroup> ageGroupList = Arrays.asList(ageGroups);
        this.acceptsFn = ageGroupList::contains;
        return this;
    }
}
