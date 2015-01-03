package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return getClub()
                .map(c -> getFirst(c).map(
                        b -> b.getSections().stream()
                                .filter(s -> !isSigned(s))
                                .findFirst()
                ).orElse(Optional.empty()))
                .orElse(Optional.empty());
    }

    private boolean isSigned(Section s) {
        return (records.containsKey(s) && records.get(s).getSigning().isPresent());
    }

    private Optional<Book> getFirst(Club c) {
        return c.getCurriculum()
                .recommendedBookList(getCurrentAgeGroup()).stream()
                .findFirst();
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
