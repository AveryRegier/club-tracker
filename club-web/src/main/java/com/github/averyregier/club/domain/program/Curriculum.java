package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.Contained;

import java.util.List;
import java.util.Optional;

/**
 * Created by avery on 9/6/2014.
 */
public interface Curriculum extends Contained<Curriculum> {
    public List<Book> getBooks();

    Optional<Section> lookup(String sectionId);
    public List<Book> recommendedBookList(AgeGroup age);

    List<Curriculum> getSeries();

    Optional<Curriculum> getSeries(String clubId);
}
