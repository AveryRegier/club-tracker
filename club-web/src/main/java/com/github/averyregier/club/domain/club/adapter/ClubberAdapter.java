package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.firstSuccess;

/**
 * Created by avery on 11/30/14.
 */
public class ClubberAdapter extends ClubMemberAdapter implements Clubber {
    private LinkedHashMap<Section, ClubberRecord> records = new LinkedHashMap<>();

    public ClubberAdapter() {
        this(new PersonAdapter());
    }

    public ClubberAdapter(Person person) {
        super(person);
        getPerson().getUpdater().setClubber(this);
    }

    public Optional<ClubberRecord> getRecord(Optional<Section> maybeASection) {
        return maybeASection.map(s -> mapToRecord(s));
    }

    public List<AwardPresentation> getAwards() {
        return records.values().stream()
                .map(ClubberRecord::getSigning)
                .filter(Optional::isPresent)
                .map(s -> s.get().getCompletionAwards())
                .filter(r->r != null)
                .flatMap(r->r.stream())
                .collect(Collectors.toList());
    }

    private ClubberRecord mapToRecord(Section section) {
        ClubberRecord record  = records.get(section);
        if(record == null) {
            record = createRecord(section);
            records.put(section, record);
        }
        return record;
    }

    private ClubberRecord createRecord(final Section s) {
        return new ClubberRecord() {
            @Override
            public Section getSection() {
                return s;
            }

            @Override
            public Clubber getClubber() {
                return ClubberAdapter.this;
            }
        };
    }

    @Override
    public Optional<Section> getNextSection() {
        return getClub().map(c -> firstSuccess(
                () -> getRequiredToMoveOn(c),
                () -> getRequiredForBook(c),
                () -> getExtraCredit(c)
        )).orElse(Optional.empty());
    }

    private Optional<Section> getRequiredToMoveOn(Club c) {
        return getClubberFutureSections(c)
                .filter(s -> s.getSectionType().requiredToMoveOn())
                .findFirst();
    }

    private Optional<Section> getRequiredForBook(Club c) {
        return getClubberFutureSections(c)
                .filter(s -> s.getSectionType().requiredFor(AccomplishmentLevel.book))
                .findFirst();
    }

    private Optional<Section> getExtraCredit(Club c) {
        return reverse(getCurrentBookList(c)).stream()
                .findFirst()
                .map(b->b.getSections().stream()
                .filter(s->!isSigned(s))
                .filter(s->!s.getSectionType().requiredFor(AccomplishmentLevel.book))
                .findFirst())
                .orElse(Optional.empty());
    }

    public static <T> List<T> reverse(List<T> original){
        ArrayList<T> toReturn = new ArrayList<T>(original);
        Collections.reverse(toReturn);
        return toReturn;
    }

    private Stream<Section> getClubberFutureSections(Club c) {
        return getClubbersSections(c)
                .filter(s -> !isSigned(s));
    }

    private Stream<Section> getClubbersSections(Club c) {
        return getCurrentBookList(c).stream()
                .flatMap(b -> b.getSections().stream());
    }

    private List<Book> getCurrentBookList(Club c) {
        return c.getCurriculum()
                .recommendedBookList(getCurrentAgeGroup());
    }

    private boolean isSigned(Section s) {
        return records.containsKey(s) && records.get(s).getSigning().isPresent();
    }

    @Override
    public List<ClubberRecord> getNextSections(int max) {
        return null;
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.of(this);
    }
}
