package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.RewardType;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SectionGroupAdapterTest{
    @Test
    public void test1section() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .addSection(new SectionBuilder(0, TnTSectionTypes.parent.get()))
                .build();
        assertEquals(sectionGroup.getSections(), sectionGroup.getSections());
        assertEquals(sectionGroup, sectionGroup.getSections().get(0).getGroup());
    }

    @Test
    public void asBook() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .build();
        assertFalse(sectionGroup.asBook().isPresent());
    }

    @Test
    public void noCompletionAward() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .addSection(new SectionBuilder(1, TnTSectionTypes.regular.get()))
                .build();
        assertTrue(sectionGroup.getSections().get(0).getRewards().isEmpty());
    }

    @Test
    public void addReward() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .addReward(new RewardBuilder()
                        .addSection(new SectionBuilder(1, TnTSectionTypes.regular.get())))
                .build(bookBuilder);
        bookBuilder.build();

        assertEquals(1, group.getSections().size());
        Section section = group.getSections().get(0);
        assertEquals(RewardType.group, section.getRewards().iterator().next().getRewardType());
        assertTrue(section.getRewards(RewardType.group).iterator().next().getSections().contains(section));
    }

    @Test
    public void add2Rewards() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .addReward(new RewardBuilder()
                        .addSection(new SectionBuilder(1, TnTSectionTypes.regular.get()))
                        .addSection(new SectionBuilder(3, TnTSectionTypes.regular.get())))
                .addReward(new RewardBuilder()
                        .addSection(new SectionBuilder(2, TnTSectionTypes.extaCredit.get())))
                .build(bookBuilder);
        bookBuilder.build();

        assertEquals(3, group.getSections().size());
        Section section1 = group.getSections().get(0);
        Section section2 = group.getSections().get(1);
        Section section3 = group.getSections().get(2);
        assertTrue(section1.getRewards(RewardType.group).iterator().next().getSections().contains(section1));
        assertTrue(section1.getRewards(RewardType.group).iterator().next().getSections().contains(section3));
        assertTrue(section3.getRewards(RewardType.group).iterator().next().getSections().contains(section1));
        assertTrue(section3.getRewards(RewardType.group).iterator().next().getSections().contains(section3));
        assertTrue(section2.getRewards(RewardType.group).iterator().next().getSections().contains(section2));
    }

}
