package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.UtilityMethods;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.asStream;
import static com.github.averyregier.club.domain.utility.UtilityMethods.firstSuccess;
import static java.util.stream.Collectors.toList;

/**
 * Created by avery on 11/30/14.
 */
public class ClubberAdapter extends ClubMemberAdapter implements Clubber {
    private LinkedHashMap<Section, ClubberRecord> records;

    public ClubberAdapter() {
        this(new PersonAdapter());
    }

    public ClubberAdapter(Person person) {
        super(person);
        getPerson().getUpdater().setClubber(this);
    }

    public Optional<ClubberRecord> getRecord(Optional<Section> maybeASection) {
        return maybeASection.map(this::mapToRecord);
    }

    public List<AwardPresentation> getAwards() {
        return getRecords().values().stream()
                .map(ClubberRecord::getSigning)
                .flatMap(UtilityMethods::stream)
                .map(Signing::getCompletionAwards)
                .filter(UtilityMethods::notNull)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    protected LinkedHashMap<Section, ClubberRecord> loadRecords() {
        return new LinkedHashMap<>();
    }

    private synchronized Map<Section, ClubberRecord> getRecords() {
        if(records == null) {
            records = loadRecords();
        }
        return records;
    }

    private ClubberRecord mapToRecord(Section section) {
        return getRecords().computeIfAbsent(section, (s) -> findRecord(section));
    }

    protected ClubberRecord findRecord(final Section s) {
        return createUnsignedRecord(s);
    }

    protected ClubberRecord createUnsignedRecord(final Section section) {
        return new ClubberRecord() {
            @Override
            public Section getSection() {
                return section;
            }

            @Override
            public Clubber getClubber() {
                return ClubberAdapter.this;
            }
        };
    }

    @Override
    public Optional<Section> getNextSection() {
        return firstSuccess(
                this::getRequiredForBooks,
                this::getExtraCredit);
    }

    private Optional<Section> getRequiredForBooks() {
        for(Book b: getCurrentBookList()) {
            Optional<Section> section = firstSuccess(
                    () -> getRequiredToMoveOn(b),
                    () -> getRequiredForBook(b));
            if(section.isPresent()) return section;
        }
        return Optional.empty();
    }

    private Optional<Section> getRequiredToMoveOn(Book b) {
        return getClubberFutureSections(b)
                .filter(s -> s.getSectionType().requiredToMoveOn())
                .findFirst();
    }

    private Optional<Section> getRequiredForBook(Book b) {
        return getRequiredForBookStream(b)
                .findFirst();
    }

    private Stream<Section> getRequiredForBookStream(Book b) {
        return getClubberFutureSections(b)
                .filter(s -> !s.getSectionType().isExtraCredit());
    }

    private Optional<Section> getExtraCredit() {
        return getAgeLevelBooks()
                .flatMap(b -> getExtraCreditLeft(b))
                .findFirst();
    }

    private Stream<Book> getAgeLevelBooks() {
        return getCurrentBookList().stream()
                .filter(this::isAgeLevelBook);
    }

    private Boolean isAgeLevelBook(Book b) {
        return b.getAgeGroups().stream().findFirst()
                .map(l -> l.equals(getCurrentAgeGroup()))
                .orElse(false);
    }

    private Stream<Section> getExtraCreditLeft(Book b) {
        return b.getSections().stream()
                .filter(s -> s.getSectionType().isExtraCredit())
                .filter(s -> !isSigned(s));
    }

    private Stream<Section> getClubberFutureSections(Book b) {
        return getClubbersSections(b)
                .filter(s -> !isSigned(s));
    }

    public Optional<Section> getSectionAfter(Section current) {
        return firstSuccess(
                () -> getCurrentBookList().stream()
                        .flatMap(this::getRequiredForBookStream)
                        .filter(s -> s.isAfter(current))
                        .findFirst(),
                () -> getAgeLevelBooks()
                        .flatMap(this::getExtraCreditLeft)
                        .filter(s -> s.isAfter(current))
                        .findFirst());
    }

    private Stream<Section> getClubbersSections(Book b) {
        return b.getSections().stream();
    }

    private List<Book> getCurrentBookList() {
        return getClub()
                .map(c -> c.getCurriculum()
                        .recommendedBookList(getCurrentAgeGroup()))
                .orElse(Collections.emptyList());
    }

    private boolean isSigned(Section s) {
        return getRecords().containsKey(s) && getRecords().get(s).getSigning().isPresent();
    }

    @Override
    public Optional<ClubberRecord> getLastRecord() {
        return asStream(records.values().stream()
                .collect(Collectors.toCollection(ArrayDeque::new)) // or LinkedList
                .descendingIterator())
                .filter(r -> r.getSigning().isPresent())
                .findFirst();
    }

    @Override
    public List<ClubberRecord> getNextSections(int max) {
        if(max == 1) {
            return getRecord(getNextSection())
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
        } else {
            List<ClubberRecord> records = getCurrentBookList().stream()
                    .flatMap(this::getRequiredForBookStream)
                    .map(s -> getRecord(Optional.of(s)).get())
                    .limit(max)
                    .collect(toList());
            if(records.size() < max) {
                int left = max - records.size();
                List<Book> books = getAgeLevelBooks().collect(Collectors.toList());
                records.addAll(books.stream()
                        .flatMap(this::getExtraCreditLeft)
                        .limit(left)
                        .map(s -> getRecord(Optional.of(s)).get())
                        .collect(toList()));
            }
            return records;
        }
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.of(this);
    }

    @Override
    public boolean hasAward(Award award) {
        return getAwards().stream()
                .map(AwardPresentation::forAccomplishment)
                .filter(n -> n.getName().equals(award.getName()))
                .findFirst()
                .isPresent();
    }
}
