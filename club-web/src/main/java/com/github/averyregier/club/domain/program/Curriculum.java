package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Contained;

import java.util.List;
import java.util.Optional;

/**
 * Created by avery on 9/6/2014.
 */
public interface Curriculum extends Contained<Curriculum> {
    List<Book> getBooks();

    Optional<Section> lookup(String sectionId);
    List<Book> recommendedBookList(AgeGroup age);

    List<Curriculum> getSeries();

    Optional<Curriculum> getSeries(String clubId);

    boolean accepts(AgeGroup ageGroup);

    Optional<Book> lookupBook(String bookId);

    default Optional<Curriculum> findCurriculum(String curriculum) {
        return Optional.empty();
    }
}
