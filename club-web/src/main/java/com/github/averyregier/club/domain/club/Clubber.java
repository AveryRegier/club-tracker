package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.github.averyregier.club.domain.utility.UtilityMethods.optMap;

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

    boolean hasAward(Award award);

    Collection<ClubberRecord> getRecords(Predicate<ClubberRecord> test);

    default Optional<Book> getBook(String bookId) {
        return optMap(getClub().map(Club::getCurriculum), c -> c.lookupBook(bookId));
    }
}
