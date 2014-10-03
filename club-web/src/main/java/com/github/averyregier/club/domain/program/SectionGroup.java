package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Contained;

/**
 * Created by avery on 9/6/2014.
 */
public interface SectionGroup extends SectionHolder, Contained<Book> {
    public int sequence();
}
