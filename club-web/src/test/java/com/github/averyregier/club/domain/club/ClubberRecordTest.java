package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.*;
import static org.junit.Assert.*;


/**
 * Created by rx39789 on 9/6/2014.
 */
public class ClubberRecordTest {

    private Clubber clubber = new MockClubber();
    private Listener me = new MockListener();
    private MockBook book = new MockBook();
    private SectionGroup section1Group = new MockSectionGroup(book, false, 1) {
        private List<Section> sections = Arrays.asList(
                new MyMockSection(0, parent, this),
                new MyMockSection(1, regular, this),
                new MyMockSection(2, friend, this),
                new MyMockSection(3, regular, this),
                new MyMockSection(4, extaCredit, this));

        @Override
        public List<Section> getSections() {
            return sections;
        }
    };

    private SectionGroup section1RewardGroup = new MockSectionGroup(book, true, -1) {
        private List<Section> sections = Arrays.asList(
                section1Group.getSections().get(0),
                section1Group.getSections().get(1),
                section1Group.getSections().get(2),
                section1Group.getSections().get(3));

        @Override
        public List<Section> getSections() {
            return sections;
        }
    };

    private SectionGroup section2Group = new MockSectionGroup(book, false, 2) {
        private List<Section> sections = Arrays.asList(
                new MyMockSection(0, parent, this),
                new MyMockSection(1, regular, this),
                new MyMockSection(2, regular, this),
                new MyMockSection(3, regular, this),
                new MyMockSection(4, extaCredit, this));

        @Override
        public List<Section> getSections() {
            return sections;
        }
    };

    private SectionGroup section2RewardGroup = new MockSectionGroup(book, true, -1) {
        private List<Section> sections = Arrays.asList(
                section2Group.getSections().get(0),
                section2Group.getSections().get(1),
                section2Group.getSections().get(2),
                section2Group.getSections().get(3));

        @Override
        public List<Section> getSections() {
            return sections;
        }
    };

    MockSectionGroup extraSectionGroup = new MockSectionGroup(book, true, -1) {
        @Override
        public List<Section> getSections() {
            final SectionGroup thisGroup = this;
            return Arrays.asList(section1Group.getSections().get(4), section2Group.getSections().get(4));
        }
    };

    private ClubberRecord createClubberRecord(final Section theSection) {
        return clubber.getRecord(Optional.ofNullable(theSection)).get();
    }

    @Test
    public void signingUnfinishedGroupsGetNoRewards() {
        ClubberRecord classUnderTest = createClubberRecord(section1Group.getSections().get(2));
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
        assertTrue(signing.getCompletionRewards().contains(record3.getSection().getRewardGroup().get().getCompletionReward().get()));
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
        assertTrue(signing.getCompletionRewards().size() == 2);
        assertTrue(signing.getCompletionRewards().contains(book.getCompletionReward().get()));
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

    private class MyMockSection extends MockSection {
        private MockSectionGroup sectionGroup;

        public MyMockSection(int sequence, TnTSectionTypes parent, MockSectionGroup mockSectionGroup) {
            super(sequence, parent.get());
            sectionGroup = mockSectionGroup;
        }

        @Override
        public SectionGroup getGroup() {
            return sectionGroup;
        }

        @Override
        public Optional<SectionGroup> getRewardGroup() {
            if(section1RewardGroup.getSections().contains(this)) {
                return Optional.of(section1RewardGroup);
            } else if(section2RewardGroup.getSections().contains(this)) {
                return Optional.of(section2RewardGroup);
            } else {
                return Optional.of(extraSectionGroup);
            }
        }
    }
}
