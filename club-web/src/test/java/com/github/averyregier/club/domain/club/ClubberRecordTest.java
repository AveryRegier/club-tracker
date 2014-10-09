package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.program.adapter.AwardBuilder;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

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
            .group(1, g->g
                    .award(a->a
                            .section(0, parent)
                            .section(1, regular)
                            .section(2, friend)
                            .section(3, regular))
                    .award(extraCreditAwardBuilder
                            .section(4, extaCredit)))
            .group(1, g->g
                    .award(a->a
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
        assertTrue(signing.getCompletionAwards().containsAll(record3.getSection().getAwards(AwardType.group)));
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
        assertEquals(2, signing.getCompletionAwards().size());
        assertTrue(signing.getCompletionAwards().containsAll(finalRecord.getSection().getAwards()));
    }

    @Test
    public void completingExtraCreditWinsExtraCreditAward() {
        ClubberRecord record1 = sign(1, 4);
        assertTrue(record1.getSigning().get().getCompletionAwards().isEmpty());
        ClubberRecord record2 = sign(2, 4);
        Set<Award> completionAwards = record2.getSigning().get().getCompletionAwards();
        assertFalse(completionAwards.isEmpty());
        assertEquals(1, completionAwards.size());
        assertTrue(completionAwards.containsAll(record2.getSection().getAwards(AwardType.group)));
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
        assertEquals(signing, record.getSigning().get());
        assertEquals(me, signing.by());
        assertEquals(LocalDate.now(), signing.getDate());
        assertEquals("Well Done!", signing.getNote());
        return signing;
    }
}
