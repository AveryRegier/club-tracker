package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Section;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by avery on 11/30/14.
 */
public class ClubberAdapter extends PersonWrapper implements Clubber {
    private LinkedHashMap<Section, ClubberRecord> records = new LinkedHashMap<>();
    private ClubAdapter club;
    private Person person = new PersonAdapter();

    public ClubberAdapter() {
        this.person.getUpdater().setClubber(this);
    }

    public Optional<ClubberRecord> getRecord(Optional<Section> maybeASection) {
        return maybeASection.map(s -> mapToRecord(s));
    }

    @Override
    protected Person getPerson() {
        return person;
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
        return null;
    }

    @Override
    public List<ClubberRecord> getNextSections(int max) {
        return null;
    }

    @Override
    public LocalDate getBirthDate() {
        return null;
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.of(this);
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public Optional<Club> getClub() {
        return Optional.ofNullable(club);
    }

    void setClub(ClubAdapter club) {
        this.club = club;
    }
}
