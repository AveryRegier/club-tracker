package com.github.averyregier.club.domain.program;

import java.util.List;
import java.util.Optional;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface SectionGroup extends SectionHolder {
    public int sequence();
    public Book getBook();
    public Optional<Book> asBook();

    String getId();
}
