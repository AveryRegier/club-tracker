package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Section;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by avery on 11/30/14.
 */
public abstract class ClubberAdapter implements Clubber {
    private LinkedHashMap<Section, ClubberRecord> records = new LinkedHashMap<>();
    private ClubAdapter club;

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
    public int getAge() {
        return 0;
    }

    @Override
    public AgeGroup getCurrentAgeGroup() {
        return null;
    }

    @Override
    public Optional<Club> getClub() {
        return Optional.ofNullable(club);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public Optional<Gender> getGender() {
        return null;
    }

    @Override
    public Optional<User> getLogin() {
        return null;
    }

    @Override
    public Optional<String> getEmail() {
        return null;
    }

    @Override
    public Optional<Parent> asParent() {
        return null;
    }

    @Override
    public Optional<Listener> asListener() {
        return null;
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.of(this);
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return null;
    }

    @Override
    public Optional<Family> getFamily() {
        return null;
    }

    void setClub(ClubAdapter club) {
        this.club = club;
    }
}
