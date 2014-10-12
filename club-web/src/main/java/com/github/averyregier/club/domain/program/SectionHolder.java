package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Named;

import java.util.List;

/**
 * Created by avery on 9/10/2014.
 */
public interface SectionHolder extends Named {
    public List<Section> getSections();
    public Book getBook();
}
