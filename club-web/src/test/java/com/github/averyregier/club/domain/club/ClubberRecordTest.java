package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.program.adapter.AwardBuilder;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.*;
import static org.junit.Assert.*;


/**
 * Created by avery on 9/6/2014.
 */
public class ClubberRecordTest {

    private Clubber clubber = new MockClubber();
    private Listener me = new MockListener();
    private AwardBuilder extraCreditAwardBuilder = new AwardBuilder();
    Book book = new BookBuilder(1)
            .award()
            .group(1, g -> g
                    .award(a -> a
                            .section(0, parent)
                            .section(1, regular)
                            .section(2, friend)
                            .section(3, regular))
                    .award(extraCreditAwardBuilder
                            .section(4, extaCredit)))
            .group(1, g -> g
                    .award(a -> a
                            .section(0, parent)
                            .section(1, regular)
                            .section(2, regular)
                            .section(3, regular))
                    .award(extraCreditAwardBuilder
                            .section(4, extaCredit)))
            .build();

    private ClubberRecord createClubberRecord(final Section theSection) {
        return clubber.getRecord(Optional.ofNullable(theSection)).get();
    }

    @Test
    public void signingUnfinishedGroupsGetNoAwards() {
        ClubberRecord classUnderTest = createClubberRecord(book.getSectionGroups().get(0).getSections().get(2));
        Signing signing = assertSigning(classUnderTest);
        assertTrue(signing.getCompletionAwards().isEmpty());
    }

    @Test
    public void lastSectionSignedWinsAward() {
        assertGroupCompleted(1);
    }

    private ClubberRecord assertGroupCompleted(int group) {
        ClubberRecord record0 = signWithoutAward(group, 0);
        ClubberRecord record1 = signWithoutAward(group, 1);
        ClubberRecord record2 = signWithoutAward(group, 2);
        ClubberRecord record3 = sign(group, 3);
        Signing signing = record3.getSigning().get();
        assertTrue(signing.getCompletionAwards().size() >= 1);
        Set<AwardPresentation> completionAwards = signing.getCompletionAwards();
        assertAwardEquals(record3.getSection().getAwards(AccomplishmentLevel.group), completionAwards);
        // completion rewards don't change on previous records
        assertNoAwards(record0);
        assertNoAwards(record1);
        assertNoAwards(record2);
        return record3;
    }

    @Test
    public void completingBookWinsBookAward() {
        assertGroupCompleted(1);
        ClubberRecord finalRecord = assertGroupCompleted(2);
        Signing signing = finalRecord.getSigning().get();
        assertEquals(signing.getCompletionAwards().size(), 2);
        assertAwardEquals(finalRecord.getSection().getAwards(), signing.getCompletionAwards());
    }

    @Test
    public void completingExtraCreditWinsExtraCreditAward() {
        ClubberRecord record1 = sign(1, 4);
        assertTrue(record1.getSigning().get().getCompletionAwards().isEmpty());
        ClubberRecord record2 = sign(2, 4);
        Set<AwardPresentation> completionAwards = record2.getSigning().get().getCompletionAwards();
        assertFalse(completionAwards.isEmpty());
        assertEquals(completionAwards.size(), 1);
        Set<Award> expected = record2.getSection().getAwards(AccomplishmentLevel.group);
        assertAwardEquals(expected, completionAwards);
    }

    private void assertAwardEquals(Set<Award> expected, Set<AwardPresentation> completionAwards) {
        assertTrue(completionAwards.stream()
                .map(p -> p.token().get().getName())
                .collect(Collectors.toSet()).containsAll(
                        expected.stream()
                                .map(a -> a.getName())
                                .collect(Collectors.toSet())));
    }

    private ClubberRecord signWithoutAward(int group, int section) {
        ClubberRecord record = sign(group, section);
        assertNoAwards(record);
        return record;
    }

    private void assertNoAwards(ClubberRecord record) {
        assertTrue(record.getSigning().get().getCompletionAwards().isEmpty());
    }

    private ClubberRecord sign(int group, int section) {
        SectionGroup sectionGroup = book.getSectionGroups().get(group - 1);
        ClubberRecord classUnderTest = createClubberRecord(
                sectionGroup.getSections().get(section));
        assertSigning(classUnderTest);
        return classUnderTest;
    }

    private Signing assertSigning(ClubberRecord record) {
        assertFalse(record.getSigning().isPresent());
        Signing signing = record.sign(me, "Well Done!");
        assertNotNull(signing);
        assertTrue(record.getSigning().isPresent());
        assertEquals(record.getSigning().get(), signing);
        assertEquals(signing.by(), me);
        assertEquals(signing.getDate(), LocalDate.now());
        assertEquals(signing.getNote(), "Well Done!");
        return signing;
    }

    @Test
    public void bookAwardSelection() {
        Curriculum curriculum = buildCurriculum();

        assertBookComplete(curriculum, "one", 0);
        assertBookComplete(curriculum, "two", 1);
    }

    private void assertBookComplete(Curriculum curriculum, String expectedAward, int bookIndex) {
        Section section2 = curriculum.getBooks().get(bookIndex).getSectionGroups().get(0).getSections().get(0);
        ClubberRecord record2 = createClubberRecord(section2);
        Signing signing2 = record2.sign(me, "Well Done!");
        assertArrayEquals(new Object[] {expectedAward}, toNameArray(signing2));
        assertTrue(signing2.getCompletionAwards().stream().allMatch(a -> a.to() == clubber));
        assertTrue(signing2.getCompletionAwards().stream()
                .allMatch(a -> a.forAccomplishment().getName().equals(Integer.toString(bookIndex+1))));
        assertTrue(signing2.getCompletionAwards().stream()
                .allMatch(a -> LocalDate.now().equals(a.earnedOn())));

    }

    @Test
    public void firstBookCompletedGetsFirstAward() {
        Curriculum curriculum = buildCurriculum();

        Section section2 = curriculum.getBooks().get(1).getSectionGroups().get(0).getSections().get(0);
        ClubberRecord record2 = createClubberRecord(section2);
        Signing signing2 = record2.sign(me, "Well Done!");
        assertArrayEquals(new Object[] {"one"}, toNameArray(signing2));
    }

    private Object[] toNameArray(Signing signing2) {
        return signing2.getCompletionAwards().stream().map(a->a.token().get().getName()).toArray();
    }

    private Curriculum buildCurriculum() {
        return new CurriculumBuilder()
                    .book(1, b -> b
                            .award(a -> a
                                    .sequence(s -> s.item(i -> i.name("one"))))
                            .group(0, g -> g
                                    .section(0, regular)))
                    .book(2, b -> b
                            .award(a -> a
                                    .sequence(s -> s
                                            .item(i -> i.name("one"))
                                            .item(i -> i.name("two"))))
                            .group(0, g -> g
                                    .section(0, regular))).build();
    }
}
