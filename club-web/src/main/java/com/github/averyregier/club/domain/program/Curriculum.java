package com.github.averyregier.club.domain.program;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Curriculum {
    public List<Book> getBooks();
    public List<AgeGroup> getAgeGroups();
    public Set<SectionType> getSectionTypes();

    Optional<Section> lookup(String sectionId);
}
