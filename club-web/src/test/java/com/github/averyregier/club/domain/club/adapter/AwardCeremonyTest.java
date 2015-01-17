package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.TestUtility;
import com.github.averyregier.club.domain.club.AwardPresentation;
import com.github.averyregier.club.domain.club.Ceremony;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Section;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.Assert.*;

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
    public void basicAwardList() {
        sign(s -> TestUtility.anyEqual(s.getContainer().getBook().sequence(), 0));

        Collection<AwardPresentation> awards = club.getAwardsNotYetPresented();
        assertNotNull(awards);
        assertEquals(2, awards.size());
        assertTrue(awards.containsAll(clubber.getAwards()));

        assertEquals(awards, program.getAwardsNotYetPresented());
    }

    private void sign(Predicate<Section> predicate) {
        sign(getClubberSections()
                .filter(predicate));
    }

    private void sign(Stream<Section> filtered) {
        filtered.forEach(s -> clubber
                .getRecord(Optional.of(s))
                .ifPresent(r -> r.sign(mockListener, "")));
    }

    private Stream<Section> getClubberSections() {
        return club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b -> b.getSections().stream());
    }

    @Test
    public void presentAward() {
        sign(s -> TestUtility.anyEqual(s.getContainer().getBook().sequence(), 0));
        Collection<AwardPresentation> allAwards = clubber.getAwards();
        Ceremony ceremony = new CeremonyAdapter();
        allAwards.stream().findFirst().ifPresent(a -> a.presentAt(ceremony));

        AwardPresentation awaredPresented = clubber.getAwards().stream().findFirst().get();
        assertEquals(ceremony, awaredPresented.presentedAt());
        assertFalse(program.getAwardsNotYetPresented().stream()
                .anyMatch(a -> a.presentedAt() != null));

    }

}