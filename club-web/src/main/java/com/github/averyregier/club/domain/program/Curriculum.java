package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.HasId;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Curriculum extends HasId {
    public List<Book> getBooks();
    public List<AgeGroup> getAgeGroups();
    public Set<SectionType> getSectionTypes();

    Optional<Section> lookup(String sectionId);
    public List<Book> recommendedBookList(AgeGroup age);
    public List<Translation> getSupportedTranslations(Locale locale);

    String getId();

}
