package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.List;

/**
* Created by avery on 9/7/2014.
*/
class SectionGroupAdapter implements SectionGroup {
    private String shortCode;
    private Later<Book> futureBook;
    private String name;
    private final List<Section> sections;
    private int sequence;

    public SectionGroupAdapter(String shortCode, Later<Book> futureBook, int sequence, String name, List<Section> sections) {
        this.shortCode = shortCode;
        this.futureBook = futureBook;
        this.name = name;
        this.sections = sections;
        this.sequence = sequence;
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public List<Section> getSections() {
        return sections;
    }

    @Override
    public Book getBook() {
        return futureBook.get();
    }

    @Override
    public String getName() {
        return name != null ? name : shortCode;
    }

    @Override
    public String getId() {
        return getContainer().getId()+":"+getShortCode();
    }

    @Override
    public String getShortCode() {
        return shortCode;
    }

    @Override
    public Book getContainer() {
        return getBook();
    }
}
