package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.utility.DisplayNamed;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.domain.utility.UtilityMethods;

import java.time.LocalDate;
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

    default Optional<DisplayNamed> findAward(Award award) {
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

    default Optional<Book> findPreviousBook(Book book, Program program) {
        Optional<Book> previous = findPrevious(book, program.getCurriculum().getBooks());
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

    default boolean isChildOf(Person person) {
        return person.asParent()
                .map(Person::getFamily)
                .map(of -> of.map(f -> f.getId().equals(getFamily().map(Family::getId).orElse(null))).orElse(false))
                .orElse(false);
    }

    default boolean maySeeRecords(Person person) {
        return  isLeaderInSameClub(person) ||
                isListenerInSameClub(person) ||
                isChildOf(person) ||
                isSamePerson(person);
    }

    default boolean maySignRecords(Person person) {
        return  isListenerInSameClub(person) &&
                !(isChildOf(person) || isSamePerson(person));
    }

    default boolean mayRecordSigning(Person person) {
        return  (isListenerInSameClub(person) ||
                isLeaderInSameClub(person)) &&
                !isSamePerson(person);
        // if a leader is also a parent, we'll still allow them to maintain section history
    }

    default Optional<Section> lookupSection(String sectionId) {
        return optMap(getClub(), c -> c.getCurriculum().lookup(sectionId));
    }

    default void catchup(Listener listener, Award award, LocalDate date, Ceremony ceremony, String note) {
        award.getSections().stream()
                .flatMap(s -> stream(getRecord(Optional.of(s))))
                .map(r -> r.catchup(listener, note, date))
                .flatMap(s->s.getCompletionAwards().stream())
                .forEach(awardPresentation -> awardPresentation.presentAt(ceremony));
    }

}
