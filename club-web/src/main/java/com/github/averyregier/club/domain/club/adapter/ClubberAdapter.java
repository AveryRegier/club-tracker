package com.github.averyregier.club.domain.club.adapter;

import com.codepoetics.protonpack.StreamUtils;
import com.codepoetics.protonpack.selectors.Selectors;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionType;
import com.github.averyregier.club.domain.utility.UtilityMethods;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.asStream;
import static com.github.averyregier.club.domain.utility.UtilityMethods.reverse;
import static java.util.stream.Collectors.mapping;
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
        if (records == null) {
            records = loadRecords();
        }
        return records;
    }

    @Override
    public Collection<ClubberRecord> getRecords(Predicate<ClubberRecord> test) {
        return getRecords().values().stream()
                .filter(test::test)
                .collect(Collectors.toList());
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
        return getNextSections(1).stream().findFirst().map(ClubberRecord::getSection);
    }

    private Stream<Section> getScheduled() {
        return getClub().map(club ->
                club.findPolicies(Policy::getNextSectionPolicy)
                        .flatMap(fn -> fn.apply(this)
                                .filter(s -> !isSigned(s))))
                .orElse(Stream.empty());
    }

    private boolean isScheduled() {
        return getClub().map(Club::isScheduled).orElse(false);
    }

    private Stream<Section> getRequiredForBookStream(Book b) {
        return getClubberFutureSections(b)
                .filter(s -> !s.getSectionType().isExtraCredit());
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
        Stream<Section>[] objectStream = getExtraCreditByType(b).values().stream().map(List::stream).toArray(Stream[]::new);
        return StreamUtils.interleave(Selectors.roundRobin(), objectStream);
    }

    private Map<SectionType, List<Section>> getExtraCreditByType(Book b) {
        return b.getSections().stream()
        .filter(s -> s.getSectionType().isExtraCredit())
        .filter(s -> !isSigned(s))
        .collect(Collectors.groupingBy(Section::getSectionType, TreeMap::new, mapping(Function.identity(), Collectors.toList())));
    }

    private Stream<Section> getClubberFutureSections(Book b) {
        return getClubbersSections(b)
                .filter(s -> !isSigned(s));
    }

    @Override
    public Optional<Section> getSectionAfter(Section current) {
        return sectionStream()
                .filter(s -> s.isAfter(current))
                .findFirst();
    }

    private Stream<Section> sectionStream() {
        return Stream.concat(
                getCurrentBookList().stream()
                        .flatMap(this::getClubbersSections)
                        .filter(s -> !s.getSectionType().isExtraCredit()),
                getAgeLevelBooks()
                        .flatMap(this::getClubbersSections)
                        .filter(s -> s.getSectionType().isExtraCredit()));
    }

    @Override
    public Optional<Section> getSectionBefore(Section current) {
        return reverse(sectionStream().collect(Collectors.toList())).stream()
                .filter(s -> s.isBefore(current))
                .findFirst();
    }

    private Stream<Section> getClubbersSections(Book b) {
        return b.getSections().stream();
    }

    private List<Book> getCurrentBookList() {
        return getClub()
                .map(c -> c.getCurrentBookList(getCurrentAgeGroup()))
                .orElse(Collections.emptyList());
    }

    private boolean isSigned(Section s) {
        return getRecords().containsKey(s) && getRecords().get(s).getSigning().isPresent();
    }

    public Stream<ClubberRecord> getRecentRecords() {
        return asStream(getRecords().values().stream()
                .collect(Collectors.toCollection(ArrayDeque::new)) // or LinkedList
                .descendingIterator())
                .filter(r -> r.getSigning().isPresent());
    }

    @Override
    public List<ClubberRecord> getNextSections(int max) {
        List<ClubberRecord> records = getCurrentBookList().stream()
                .flatMap(this::getUpcomingSectionsStream)
                .map(s -> getRecord(Optional.of(s)).get())
                .distinct()
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

    public Stream<Section> getUpcomingSectionsStream(Book b) {
        if(isScheduled()) {
            return getScheduled();
        }
        return Stream.concat(
                getRequiredToMoveOn(b),
                getRequiredForBookStream(b)
        );
    }

    private Stream<Section> getRequiredToMoveOn(Book b) {
        return getClubberFutureSections(b)
                .filter(s -> s.getSectionType().requiredToMoveOn());
    }

}
