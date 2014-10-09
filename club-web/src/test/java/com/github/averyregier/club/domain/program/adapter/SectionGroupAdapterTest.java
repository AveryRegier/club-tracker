package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.AwardType;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SectionGroupAdapterTest{
    @Test
    public void test1section() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .section(new SectionBuilder(0, TnTSectionTypes.parent.get()))
                .build();
        assertEquals(sectionGroup.getSections(), sectionGroup.getSections());
        assertEquals(sectionGroup, sectionGroup.getSections().get(0).getGroup());
    }

    @Test
    public void noCompletionAward() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .section(new SectionBuilder(1, TnTSectionTypes.regular.get()))
                .build();
        assertTrue(sectionGroup.getSections().get(0).getAwards().isEmpty());
    }

    @Test
    public void addAward() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .award(new AwardBuilder()
                        .section(new SectionBuilder(1, TnTSectionTypes.regular.get())))
                .build(bookBuilder);
        bookBuilder.build();

        assertEquals(1, group.getSections().size());
        Section section = group.getSections().get(0);
        assertEquals(AwardType.group, section.getAwards().iterator().next().getAwardType());
        assertTrue(section.getAwards(AwardType.group).iterator().next().getSections().contains(section));
    }

    @Test
    public void addAwardDefaultName() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .name("Discovery 1")
                .award(new AwardBuilder()
                        .section(new SectionBuilder(1, TnTSectionTypes.regular.get())))
                .build(bookBuilder);
        bookBuilder.build();

        Section section = group.getSections().get(0);
        assertEquals("Discovery 1", section.getAwards(AwardType.group).iterator().next().getName());
    }

    @Test
    public void add2Awards() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .award(new AwardBuilder()
                        .section(new SectionBuilder(1, TnTSectionTypes.regular.get()))
                        .section(new SectionBuilder(3, TnTSectionTypes.regular.get())))
                .award(new AwardBuilder()
                        .section(new SectionBuilder(2, TnTSectionTypes.extaCredit.get())))
                .build(bookBuilder);
        bookBuilder.build();

        assertEquals(3, group.getSections().size());
        Section section1 = group.getSections().get(0);
        Section section2 = group.getSections().get(1);
        Section section3 = group.getSections().get(2);
        assertTrue(section1.getAwards(AwardType.group).iterator().next().getSections().contains(section1));
        assertTrue(section1.getAwards(AwardType.group).iterator().next().getSections().contains(section3));
        assertTrue(section3.getAwards(AwardType.group).iterator().next().getSections().contains(section1));
        assertTrue(section3.getAwards(AwardType.group).iterator().next().getSections().contains(section3));
        assertTrue(section2.getAwards(AwardType.group).iterator().next().getSections().contains(section2));
    }

}
