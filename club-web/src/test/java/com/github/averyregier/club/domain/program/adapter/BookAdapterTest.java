package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.RewardType;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BookAdapterTest {


    @Test
    public void getAllSectionsFor1SectionBook() {
        Book classUnderTest = new BookBuilder(1)
                .addSectionGroup(new SectionGroupBuilder(1)
                        .addSection(new SectionBuilder(0, TnTSectionTypes.parent.get())))
                .build();
        assertNotNull(classUnderTest);
        assertEquals(1,classUnderTest.sequence());
        assertNotNull(classUnderTest.getSectionGroups());
        assertEquals(1, classUnderTest.getSectionGroups().size());
        assertEquals(1, classUnderTest.getSectionGroups().get(0).sequence());
        assertEquals(classUnderTest.getSectionGroups(), classUnderTest.getSectionGroups());
        assertEquals(classUnderTest, classUnderTest.getSectionGroups().get(0).getBook());
        List<Section> sections = classUnderTest.getSections();
        assertNotNull(sections);
        assertEquals(1, sections.size());
        assertEquals(0, sections.get(0).sequence());
    }

    @Test
    public void getAllSectionsFor2SectionBook() {
        Book classUnderTest = new BookBuilder(1)
                .addSectionGroup(new SectionGroupBuilder(1)
                        .addSection(new SectionBuilder(0, TnTSectionTypes.parent.get()))
                        .addSection(new SectionBuilder(1, TnTSectionTypes.parent.get())))
                .build();
        assertNotNull(classUnderTest);
        assertEquals(1,classUnderTest.sequence());
        assertNotNull(classUnderTest.getSectionGroups());
        assertEquals(1, classUnderTest.getSectionGroups().size());
        assertEquals(1, classUnderTest.getSectionGroups().get(0).sequence());
        assertEquals(classUnderTest.getSectionGroups(), classUnderTest.getSectionGroups());
        List<Section> sections = classUnderTest.getSections();
        assertNotNull(sections);
        assertEquals(2, sections.size());
        assertEquals(0, sections.get(0).sequence());
        assertEquals(1, sections.get(1).sequence());
    }

    @Test
    public void getAllSectionsFor2SectionGroups() {
        Book classUnderTest = new BookBuilder(1)
                .addSectionGroup(new SectionGroupBuilder(1)
                        .addSection(new SectionBuilder(0, TnTSectionTypes.parent.get()))
                        .addSection(new SectionBuilder(1, TnTSectionTypes.parent.get())))
                .addSectionGroup(new SectionGroupBuilder(2)
                        .addSection(new SectionBuilder(0, TnTSectionTypes.parent.get()))
                        .addSection(new SectionBuilder(1, TnTSectionTypes.parent.get())))
                .build();
        assertNotNull(classUnderTest);
        assertEquals(1, classUnderTest.sequence());
        assertNotNull(classUnderTest.getSectionGroups());
        assertEquals(2, classUnderTest.getSectionGroups().size());
        assertEquals(1, classUnderTest.getSectionGroups().get(0).sequence());
        assertEquals(2, classUnderTest.getSectionGroups().get(1).sequence());
        assertEquals(classUnderTest.getSectionGroups(), classUnderTest.getSectionGroups());
        List<Section> sections = classUnderTest.getSections();
        assertNotNull(sections);
        assertEquals(4, sections.size());
        assertEquals(0, sections.get(0).sequence());
        assertEquals(1, sections.get(1).sequence());
        assertEquals(0, sections.get(2).sequence());
        assertEquals(1, sections.get(3).sequence());
    }

    @Test public void asBook() {
        Book classUnderTest = new BookBuilder(1).build();
        assertSame(classUnderTest, classUnderTest.asBook().get());
    }

    @Test public void getBook() {
        Book classUnderTest = new BookBuilder(1).build();
        assertSame(classUnderTest, classUnderTest.getBook());
    }

//    @Test public void noCompletionAward() {
//        Book classUnderTest = new BookBuilder(1).build();
//        assertFalse(classUnderTest.getCompletionReward().isPresent());
//    }

    @Test
    public void bookRewardType() {
        Book classUnderTest = new BookBuilder(1)
                .addReward(new RewardBuilder())
                .addSectionGroup(new SectionGroupBuilder(1)
                        .addSection(new SectionBuilder(0, TnTSectionTypes.parent.get())))
                .build();
        assertEquals(RewardType.book,
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getRewardType());
    }
}