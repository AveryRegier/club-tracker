package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.RewardBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.*;
import static org.junit.Assert.*;


/**
 * Created by rx39789 on 9/6/2014.
 */
public class ClubberRecordTest {

    private Clubber clubber = new MockClubber();
    private Listener me = new MockListener();
    private RewardBuilder extraCreditRewardBuilder = new RewardBuilder();
    Book book = new BookBuilder(1)
            .addReward(new RewardBuilder())
            .addSectionGroup(new SectionGroupBuilder(1)
                    .addReward(new RewardBuilder()
                            .addSection(new SectionBuilder(0, parent.get()))
                            .addSection(new SectionBuilder(1, regular.get()))
                            .addSection(new SectionBuilder(2, friend.get()))
                            .addSection(new SectionBuilder(3, regular.get())))
                    .addReward(extraCreditRewardBuilder
                        .addSection(new SectionBuilder(4, extaCredit.get()))))
            .addSectionGroup(new SectionGroupBuilder(1)
                    .addReward(new RewardBuilder()
                            .addSection(new SectionBuilder(0, parent.get()))
                            .addSection(new SectionBuilder(1, regular.get()))
                            .addSection(new SectionBuilder(2, regular.get()))
                            .addSection(new SectionBuilder(3, regular.get())))
                    .addReward(extraCreditRewardBuilder
                            .addSection(new SectionBuilder(4, extaCredit.get()))))
            .build();

    private ClubberRecord createClubberRecord(final Section theSection) {
        return clubber.getRecord(Optional.ofNullable(theSection)).get();
    }

    @Test
    public void signingUnfinishedGroupsGetNoRewards() {
        ClubberRecord classUnderTest = createClubberRecord(book.getSectionGroups().get(0).getSections().get(2));
        Signing signing = assertSigning(classUnderTest);
        assertTrue(signing.getCompletionRewards().isEmpty());
    }

    @Test
    public void lastSectionSignedWinsReward() {
        assertGroupCompleted(1);
    }

    private ClubberRecord assertGroupCompleted(int group) {
        ClubberRecord record0 = signWithoutReward(group, 0);
        ClubberRecord record1 = signWithoutReward(group, 1);
        ClubberRecord record2 = signWithoutReward(group, 2);
        ClubberRecord record3 = sign(group, 3);
        Signing signing = record3.getSigning().get();
        assertTrue(signing.getCompletionRewards().size() >= 1);
        assertTrue(signing.getCompletionRewards().containsAll(record3.getSection().getRewards(RewardType.group)));
        // completion rewards don't change on previous records
        assertNoRewards(record0);
        assertNoRewards(record1);
        assertNoRewards(record2);
        return record3;
    }

    @Test
    public void completingBookWinsBookReward() {
        assertGroupCompleted(1);
        ClubberRecord finalRecord = assertGroupCompleted(2);
        Signing signing = finalRecord.getSigning().get();
        assertEquals(2, signing.getCompletionRewards().size());
        assertTrue(signing.getCompletionRewards().containsAll(finalRecord.getSection().getRewards()));
    }

    @Test
    public void completingExtraCreditWinsExtraCreditReward() {
        ClubberRecord record1 = sign(1, 4);
        assertTrue(record1.getSigning().get().getCompletionRewards().isEmpty());
        ClubberRecord record2 = sign(2, 4);
        Set<Reward> completionRewards = record2.getSigning().get().getCompletionRewards();
        assertFalse(completionRewards.isEmpty());
        assertEquals(1, completionRewards.size());
        assertTrue(completionRewards.containsAll(record2.getSection().getRewards(RewardType.group)));
    }

    private ClubberRecord signWithoutReward(int group, int section) {
        ClubberRecord record = sign(group, section);
        assertNoRewards(record);
        return record;
    }

    private void assertNoRewards(ClubberRecord record) {
        assertTrue(record.getSigning().get().getCompletionRewards().isEmpty());
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
