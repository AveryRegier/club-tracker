package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Section;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by avery on 3/3/15.
 */
public class DomainTestUtil {
    static Clubber registerClubber(ProgramAdapter program, AgeGroup.DefaultAgeGroup grade, Person.Gender gender) {
        ClubberAdapter clubber = new ClubberAdapter(new PersonAdapter());
        clubber.getUpdater().setAgeGroup(grade);
        clubber.getUpdater().setGender(gender);
        program.register(clubber);
        return clubber;
    }

    static Club createClubAdapter(ProgramAdapter program, Curriculum o) {
        return new ClubAdapter(o) {
            @Override
            public Program getProgram() {
                return program;
            }
        };
    }

    static boolean allSectionsUnique(List<ClubberRecord> nextSections) {
        return getSectionStream(nextSections).collect(Collectors.toSet()).size() == nextSections.size();
    }

    static Stream<Section> getSectionStream(List<ClubberRecord> nextSections) {
        return nextSections.stream().map(ClubberRecord::getSection);
    }

    static List<ClubberRecord> assertNextSections(ClubberAdapter clubber, int numSections) {
        List<ClubberRecord> nextSections = clubber.getNextSections(numSections);
        assertNotNull(nextSections);
        assertEquals(numSections, nextSections.size());
        assertTrue(allSectionsUnique(nextSections));
        assertTrue(nextSections.stream().allMatch(r -> !r.getSigning().isPresent()));
        assertTrue(nextSections.stream().allMatch(r -> r.getClubber().equals(clubber)));
        return nextSections;
    }
}
