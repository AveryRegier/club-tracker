package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Curriculum;
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
                .section(0, TnTSectionTypes.parent)
                .build();
        assertEquals(sectionGroup.getSections(), sectionGroup.getSections());
        assertEquals(sectionGroup, sectionGroup.getSections().get(0).getGroup());
    }

    @Test
    public void noCompletionAward() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .section(1, TnTSectionTypes.regular)
                .build();
        assertTrue(sectionGroup.getSections().get(0).getAwards().isEmpty());
    }

    @Test
    public void addAward() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .award(a->a
                        .section(1, TnTSectionTypes.regular))
                .build(bookBuilder);
        bookBuilder.build();

        assertEquals(1, group.getSections().size());
        Section section = group.getSections().get(0);
        assertEquals(AccomplishmentLevel.group, section.getAwards().iterator().next().getAccomplishmentLevel());
        assertTrue(section.getAwards(AccomplishmentLevel.group).iterator().next().getSections().contains(section));
    }

    @Test
    public void addAwardDefaultName() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .name("Discovery 1")
                .award(a -> a
                        .section(1, TnTSectionTypes.regular))
                .build(bookBuilder);
        bookBuilder.build();

        Section section = group.getSections().get(0);
        assertEquals("Discovery 1", section.getAwards(AccomplishmentLevel.group).iterator().next().getName());
    }

    @Test
    public void add2Awards() {
        BookBuilder bookBuilder = new BookBuilder(0);
        SectionGroup group = new SectionGroupBuilder(1)
                .award(a -> a
                        .section(1, TnTSectionTypes.regular)
                        .section(3, TnTSectionTypes.regular))
                .award(a -> a
                        .section(2, TnTSectionTypes.extraCredit))
                .build(bookBuilder);
        bookBuilder.build();

        assertEquals(3, group.getSections().size());
        Section section1 = group.getSections().get(0);
        Section section2 = group.getSections().get(1);
        Section section3 = group.getSections().get(2);
        assertTrue(section1.getAwards(AccomplishmentLevel.group).iterator().next().getSections().contains(section1));
        assertTrue(section1.getAwards(AccomplishmentLevel.group).iterator().next().getSections().contains(section3));
        assertTrue(section3.getAwards(AccomplishmentLevel.group).iterator().next().getSections().contains(section1));
        assertTrue(section3.getAwards(AccomplishmentLevel.group).iterator().next().getSections().contains(section3));
        assertTrue(section2.getAwards(AccomplishmentLevel.group).iterator().next().getSections().contains(section2));
    }


    @Test
    public void testSpecificId() {
        Curriculum curriculum = new CurriculumBuilder()
                .shortCode("A")
                .book(1, b -> b
                        .shortCode("B")
                        .publicationYear(1)
                        .group(2, g -> g
                                .shortCode("ID"))) // what we're actually testing
                .build();
        assertEquals("A:B©1:ID", curriculum.getBooks().get(0).getSectionGroups().get(0).getId());
    }
}
