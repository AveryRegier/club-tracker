package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Reward;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;

import java.util.List;
import java.util.Optional;

/**
* Created by rx39789 on 9/7/2014.
*/
class SectionGroupAdapter implements SectionGroup {
    private final List<Section> sections;
    private int sequence;

    public SectionGroupAdapter(int sequence, List<Section> sections) {
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
        return null;
    }

    @Override
    public Optional<Book> asBook() {
        return Optional.empty();
    }

    @Override
    public Optional<Reward> getCompletionReward() {
        return Optional.empty();
    }

    @Override
    public String getId() {
        return null;
    }
}
