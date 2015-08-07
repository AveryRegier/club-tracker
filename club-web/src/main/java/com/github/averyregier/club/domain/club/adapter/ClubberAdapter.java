package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.UtilityMethods;

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
        return maybeASection.map(this::mapToRecord);
    }

    public List<AwardPresentation> getAwards() {
        return records.values().stream()
                .map(ClubberRecord::getSigning)
                .filter(Optional::isPresent)
                .map(s->s.get().getCompletionAwards())
                .filter(r->r != null)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private ClubberRecord mapToRecord(Section section) {
        return records.computeIfAbsent(section, (s)-> createRecord(section));
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

    public ClubberRecord addRecord(final Section s, final Signing signing) {
        ClubberRecord record = new ClubberRecord() {
            @Override
            public Section getSection() {
                return s;
            }

            @Override
            public Clubber getClubber() {
                return ClubberAdapter.this;
            }

            @Override
            public Optional<Signing> getSigning() {
                return Optional.of(signing);
            }
        };
        records.put(s, record);
        return record;
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
                .filter(s->s.getSectionType().requiredToMoveOn())
                .findFirst();
    }

    private Optional<Section> getRequiredForBook(Book b) {
        return getRequiredForBookStream(b)
                .findFirst();
    }

    private Stream<Section> getRequiredForBookStream(Book b) {
        return getClubberFutureSections(b)
                .filter(s->s.getSectionType().requiredFor(AccomplishmentLevel.book));
    }

    private Optional<Section> getExtraCredit() {
        return getAgeLevelBook()
                .map(b -> getExtraCreditLeft(b).findFirst())
                .orElse(Optional.empty());
    }

    private Optional<Book> getAgeLevelBook() {
        return UtilityMethods.reverse(getCurrentBookList()).stream()
                .findFirst();
    }

    private Stream<Section> getExtraCreditLeft(Book b) {
        return b.getSections().stream()
                .filter(s -> !isSigned(s))
                .filter(s -> !s.getSectionType().requiredFor(AccomplishmentLevel.book));
    }

    private Stream<Section> getClubberFutureSections(Book b) {
        return getClubbersSections(b)
                .filter(s -> !isSigned(s));
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
        return records.containsKey(s) && records.get(s).getSigning().isPresent();
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
                    .collect(Collectors.toList());
            if(records.size() < max) {
                int left = max - records.size();
                records.addAll(getAgeLevelBook()
                        .map(b -> getExtraCreditLeft(b)
                                .limit(left)
                                .map(s -> getRecord(Optional.of(s)).get())
                                .collect(Collectors.toList()))
                        .orElse(Collections.emptyList()));
            }
            return records;
        }
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.of(this);
    }
}
