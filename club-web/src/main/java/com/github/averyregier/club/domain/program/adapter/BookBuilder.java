package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.SectionGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by rx39789 on 9/7/2014.
 */
public class BookBuilder {
    private List<SectionGroupBuilder> sectionGroupBuilders = new ArrayList<>();
    private int sequence;

    public BookBuilder(int sequence) {
        this.sequence = sequence;
    }

    public Book build() {
        Later<Book> futureBook = new Later<>();
        List<SectionGroup> sectionGroups = buildSectionGroups(futureBook);
        BookAdapter book = new BookAdapter() {
            @Override
            public int sequence() {
                return sequence;
            }

            @Override
            public List<SectionGroup> getSectionGroups() {
                return sectionGroups;
            }
        };
        futureBook.set(book);
        return book;
    }

    private List<SectionGroup> buildSectionGroups(Later<Book> futureBook) {

        return sectionGroupBuilders.stream().map(b -> b.setBook(futureBook).build()).collect(Collectors.toList());
    }

    public BookBuilder addSectionGroup(SectionGroupBuilder sectionGroupBuilder) {
        this.sectionGroupBuilders.add(sectionGroupBuilder);
        return this;
    }
}
