package com.github.averyregier.club.domain.program;

import java.util.List;
import java.util.Optional;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface SectionGroup {
    public int sequence();
    public List<Section> getSections();
    public Book getBook();
    public Optional<Book> asBook();
    public Optional<Reward> getCompletionReward();

    String getId();
}
