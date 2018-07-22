package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Contained;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.domain.utility.Setting;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/6/2014.
 */
public interface Curriculum extends Contained<Curriculum>, Named {
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

    default Collection<AgeGroup> getAgeGroups() {
        return getBooks().stream()
                .flatMap(b -> b.getAgeGroups().stream())
                .distinct()
                .sorted(new AgeGroup.Comparator())
                .collect(Collectors.toList());
    }

    default List<Curriculum> getAllSeries() {
        Set<Curriculum> allSeries = new LinkedHashSet<>(getSeries());
        while (allSeries.addAll(allSeries.stream()
                .flatMap(c -> c.getSeries().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new)))) ;
        return new ArrayList<>(allSeries);
    }

    default boolean isScheduled() {
        return false;
    }

    default List<Section> getScheduledSections() {
        return getBooks().stream()
                .flatMap(b -> b.getSections().stream())
                .filter(s -> !s.getSectionType().isExtraCredit())
                .collect(Collectors.toList());
    }

    class Type implements Setting.Type<Curriculum> {

        private static Type type = new Type();

        public static Type get() {
            return type;
        }

        @Override
        public String marshall(Curriculum thing) {
            return thing.getId();
        }

        @Override
        public Optional<Curriculum> unmarshall(String value) {
            return Programs.find(value);
        }
    }
}
