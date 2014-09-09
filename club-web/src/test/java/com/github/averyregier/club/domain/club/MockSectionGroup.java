package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.*;

import java.util.Optional;

/**
* Created by rx39789 on 9/6/2014.
*/
abstract class MockSectionGroup implements SectionGroup {
    private Reward reward = new Reward() {};
    private Book book;
    private int sequence;

    public MockSectionGroup(MockBook book, boolean isReward, int sequence) {
        this.book = book;
        this.sequence = sequence;
        if(!isReward) {
            book.addSectionGroup(this);
        }
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public Book getBook() {
        return book;
    }

    @Override
    public Optional<Book> asBook() {
        return Optional.empty();
    }

    @Override
    public Optional<Reward> getCompletionReward() {
        return Optional.of(reward);
    }

    @Override
    public String getId() {
        return Integer.toString(sequence());
    }

}
