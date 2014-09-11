package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.*;
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

    private Reward section1RewardGroup = new Reward() {
        private List<Section> sections = Arrays.asList(
                section1Group.getSections().get(0),
                section1Group.getSections().get(1),
                section1Group.getSections().get(2),
                section1Group.getSections().get(3));

        @Override
        public List<Section> getSections() {
            return sections;
        }

        @Override
        public RewardType getRewardType() {
            return RewardType.group;
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

    private Reward section2RewardGroup = new Reward() {
        private List<Section> sections = Arrays.asList(
                section2Group.getSections().get(0),
                section2Group.getSections().get(1),
                section2Group.getSections().get(2),
                section2Group.getSections().get(3));

        @Override
        public List<Section> getSections() {
            return sections;
        }

        @Override
        public RewardType getRewardType() {
            return RewardType.group;
        }
    };

    Reward extraSectionGroup = new Reward() {
        @Override
        public List<Section> getSections() {
            return Arrays.asList(section1Group.getSections().get(4), section2Group.getSections().get(4));
        }

        @Override
        public RewardType getRewardType() {
            return RewardType.group;
        }
    };

    Reward bookReward = new Reward() {
        @Override
        public RewardType getRewardType() {
            return RewardType.book;
        }

        @Override
        public List<Section> getSections() {
            return book.getSections().stream()
                    .filter(s->s.getSectionType().requiredForBookReward())
                    .collect(Collectors.toList());
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
        assertTrue(signing.getCompletionRewards().contains(bookReward));
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
        public Set<Reward> getRewards(RewardType type) {
            return  getRewards().stream()
                    .filter(r -> r.getRewardType() == type)
                    .collect(Collectors.toSet());
        }

        @Override
        public Set<Reward> getRewards() {
            Reward forSectionGroup;
            if(section1RewardGroup.getSections().contains(this)) {
                forSectionGroup = section1RewardGroup;
            } else if(section2RewardGroup.getSections().contains(this)) {
                forSectionGroup = section2RewardGroup;
            } else {
                forSectionGroup = extraSectionGroup;
            }
            return new HashSet<>(Arrays.asList(forSectionGroup, bookReward));
        }
    }
}
