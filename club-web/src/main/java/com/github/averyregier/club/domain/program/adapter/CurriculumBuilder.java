package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/15/14.
 */
public class CurriculumBuilder {
    private String shortCode;
    private List<BookBuilder> books = new ArrayList<>();

    public Curriculum build() {
        Later<Curriculum> curriculumLater = new Later<>();
        List<Book> bookList = buildBooks(curriculumLater);
        CurriculumAdapter curriculum = new CurriculumAdapter(shortCode, bookList);
        curriculumLater.set(curriculum);
        return curriculum;
    }

    private List<Book> buildBooks(Later<Curriculum> curriculumLater) {
        return books.stream().map(b -> b.setCurriculum(curriculumLater).build()).collect(Collectors.toList());
    }

    public CurriculumBuilder shortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public CurriculumBuilder book(int sequence, Function<BookBuilder, BookBuilder> function) {

        books.add(function.apply(new BookBuilder(sequence)));
        return this;
    }

}
