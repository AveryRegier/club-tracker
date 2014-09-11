package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Section;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by rx39789 on 9/6/2014.
 */
public class MockClubber implements Clubber {
    private LinkedHashMap<Section, ClubberRecord> records = new LinkedHashMap<>();
    @Override
    public Optional<Section> getNextSection() {
        return null;
    }

    @Override
    public List<ClubberRecord> getNextSections(int max) {
        return null;
    }

    @Override
    public Optional<ClubberRecord> getRecord(Optional<Section> maybeASection) {
        return maybeASection.map(s->mapToRecord(s));
    }

    private ClubberRecord mapToRecord(Section section) {
        ClubberRecord record  = records.get(section);
        if(record == null) {
            record = createRecord(section);
            records.put(section, record);;
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
                return MockClubber.this;
            }
        };
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
        return null;
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
    public Optional<Parent> asParent() {
        return null;
    }

    @Override
    public Optional<Listener> asListener() {
        return null;
    }

    @Override
    public Optional<Clubber> asClubber() {
        return null;
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return null;
    }
}