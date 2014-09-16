package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.Translation;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static com.github.averyregier.club.domain.program.RewardType.book;
import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.parent;
import static org.junit.Assert.*;

public class BookAdapterTest {


    @Test
    public void getAllSectionsFor1SectionBook() {
        Book classUnderTest = new BookBuilder(1)
                .addSectionGroup(1, g->g
                        .addSection(0, parent))
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
                .addSectionGroup(1, g -> g
                        .addSection(0, parent)
                        .addSection(1, parent))
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
                .addSectionGroup(1,g->g
                        .addSection(0, parent)
                        .addSection(1, parent))
                .addSectionGroup(2, g -> g
                        .addSection(0, parent)
                        .addSection(1, parent))
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
                .addSectionGroup(1, g -> g
                        .addSection(0, parent))
                .build();
        assertEquals(book,
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getRewardType());
    }

    @Test
    public void setBookVersion() {
        Book classUnderTest = new BookBuilder(4)
                .setVersion(1,0)
                .build();
        assertEquals(1, classUnderTest.getVersion().major());
        assertEquals(0, classUnderTest.getVersion().minor());
    }

    @Test
    public void setId() {
        assertEquals("foobar", new BookBuilder(1).setShortCode("foobar").build().getShortCode());
    }

    @Test
    public void translation() {
        Translation translation = new Translation() {
        };
        assertEquals(translation, new BookBuilder(3).setTranslation(translation).build().getVersion().getTranslation());
    }

    @Test
    public void noTranslation() {
        assertEquals(Translation.none, new BookBuilder(3).build().getVersion().getTranslation());
    }

    @Test
    public void language() {
        assertEquals(Locale.FRENCH, new BookBuilder(3).setLanguage(Locale.FRENCH).build().getVersion().getLanguage());
    }

    @Test
    public void noLanguage() {
        assertEquals(Translation.none, new BookBuilder(3).build().getVersion().getTranslation());
    }
}