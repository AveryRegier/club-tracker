package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.TestUtility;
import com.github.averyregier.club.domain.club.AwardPresentation;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.program.AgeGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AwardCeremonyTest {
    private ProgramAdapter program;
    private Club club;
    private ClubberAdapter clubber;
    private ListenerAdapter mockListener = new ListenerAdapter(new PersonAdapter());

    @Before
    public void setup() {
        program = new ProgramAdapter("en_US", null, "AWANA");
        club = program.addClub(program.getCurriculum().getSeries("TnT").get());
        clubber = new ClubberAdapter();
        clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.FIFTH_GRADE);
        program.register(clubber);
    }

    @Test
    public void testBasicAwardList() {
        club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b -> b.getSections().stream())
                .filter(s -> TestUtility.anyEqual(s.getContainer().getBook().sequence(), 0))
                .forEach(s->clubber.getRecord(Optional.of(s)).ifPresent(r -> r.sign(mockListener, "")));

        Collection<AwardPresentation> awards = club.getAwardsNotYetPresented();
        assertNotNull(awards);
        assertEquals(2, awards.size());
        assertTrue(awards.containsAll(clubber.getAwards()));
    }
}