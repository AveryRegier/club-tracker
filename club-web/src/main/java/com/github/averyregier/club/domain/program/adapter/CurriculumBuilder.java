package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.List;
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

    public Curriculum build() {
        Later<Curriculum> curriculumLater = new Later<>();
        CurriculumAdapter curriculum;
        if(books.isEmpty()) {
            List<Curriculum> series = this.series.stream()
                    .map(s->s.setCurriculum(curriculumLater).build())
                    .collect(Collectors.toList());
            curriculum = new MasterCurriculum(shortCode, series);
        } else {
            List<Book> bookList = buildBooks(curriculumLater);
            curriculum = new CurriculumAdapter(shortCode, bookList, parentCurriculum);
        }
        curriculumLater.set(curriculum);
        return curriculum;
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

    public CurriculumBuilder book(int sequence, UnaryOperator<BookBuilder> function) {

        books.add(function.apply(new BookBuilder(sequence)));
        return this;
    }

    public CurriculumBuilder curriculum(UnaryOperator<CurriculumBuilder> f) {
        series.add(f.apply(new CurriculumBuilder()));
        return this;
    }
}
