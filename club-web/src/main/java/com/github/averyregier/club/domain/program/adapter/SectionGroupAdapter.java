package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;

import java.util.List;

/**
* Created by avery on 9/7/2014.
*/
class SectionGroupAdapter implements SectionGroup {
    private Later<Book> futureBook;
    private final List<Section> sections;
    private int sequence;

    public SectionGroupAdapter(Later<Book> futureBook, int sequence, List<Section> sections) {
        this.futureBook = futureBook;
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
    public String getId() {
        return getContainer().getId()+":"+getShortCode();
    }

    @Override
    public String getShortCode() {
        return Integer.toString(sequence());
    }

    @Override
    public Book getContainer() {
        return getBook();
    }
}
