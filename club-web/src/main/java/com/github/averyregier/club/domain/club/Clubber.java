package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.domain.utility.UtilityMethods;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;
import static java.util.stream.Collectors.toList;

/**
 * Created by avery on 9/5/2014.
 */
public interface Clubber extends ClubMember {
    Optional<Section> getNextSection();

    Optional<ClubberRecord> getLastRecord();

    List<ClubberRecord> getNextSections(int max);

    Optional<ClubberRecord> getRecord(Optional<Section> section);

    List<AwardPresentation> getAwards();

    Optional<Section> getSectionAfter(Section current);
    Optional<Section> getSectionBefore(Section current);


    Collection<ClubberRecord> getRecords(Predicate<ClubberRecord> test);

    default Optional<Clubber> asClubber() {
        return Optional.of(this);
    }

    default boolean hasAward(Award award) {
        return findAward(award)
                .isPresent();
    }

    default Optional<Named> findAward(Award award) {
        return getAwards().stream()
                .map(AwardPresentation::forAccomplishment)
                .filter(n -> n.equals(award))
                .findFirst();
    }

    default Optional<AwardPresentation> findPresentation(Book book, String awardName) {
        return getRecords(r->r.getSection().getGroup().getBook().equals(book))
                .stream()
                .flatMap(r -> stream(r.getSigning()))
                .flatMap(s -> s.getCompletionAwards().stream())
                .filter(n -> n.forAccomplishment().getName().equals(awardName))
                .findFirst();
    }

    default Optional<Book> getBook(String bookId) {
        return optMap(getClub().map(Club::getCurriculum), c -> c.lookupBook(bookId));
    }

    default Map<Award, Optional<AwardPresentation>> getBookAwards(Book book) {
        Map<Named, AwardPresentation> awarded = getAwards().stream()
                .collect(Collectors.toMap(AwardPresentation::forAccomplishment, Function.identity(), (u, v) -> u));
        Map<Award, Optional<AwardPresentation>> map = new LinkedHashMap<>();
        book.getSections().stream()
                .flatMap(s -> s.getAwards().stream())
                .filter(UtilityMethods::notNull)
                .distinct()
                .forEach(a-> map.put(a, Optional.ofNullable(awarded.get(a))));
        return map;
    }

    default Optional<Book> findPreviousBook(Book book) {
        Optional<Book> previous = findPrevious(book, book.getContainer().getBooks());
        if(previous.isPresent()) {
            Optional<Book> after = previous;
            do {
                previous = after;
                after = findNextBook(after.get());
            } while(after.isPresent() && after.get() != book);
        }
        return previous;
    }

    default Optional<Book> findNextBook(Book book) {
        // get any optional extra books that may have been done in a previous year
        LinkedHashSet<Book> bookList = new LinkedHashSet<>(book.getContainer().recommendedBookList(book.getAgeGroups().get(0)));
        bookList.addAll(book.getContainer().recommendedBookList(this.getCurrentAgeGroup()));

        return findNext(book, bookList);
    }

    default Map<SectionGroup, List<ClubberRecord>> getBookRecordsByGroup(Book book) {
        return book.getSectionGroups().stream()
                .flatMap(g -> g.getSections().stream())
                .map(s -> getRecord(Optional.of(s)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(r -> r.getSection().getContainer(), LinkedHashMap::new, toList()));
    }

    default Optional<Book> getLastBook() {
        return optMap(getClub(), c -> findLast(c.getCurriculum().getBooks()));
    }

    default Optional<Listener> getLastListener() {
        return chain(getLastRecord(), ClubberRecord::getSigning).map(Signing::by);
    }

}
