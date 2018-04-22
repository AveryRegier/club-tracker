package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import org.junit.Test;

import java.util.*;

import static com.github.averyregier.club.domain.program.AccomplishmentLevel.book;
import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.parent;
import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.regular;
import static org.junit.Assert.*;

public class BookAdapterTest {


    @Test
    public void getAllSectionsFor1SectionBook() {
        Book classUnderTest = new BookBuilder(1)
                .group(1, g -> g
                        .section(0, parent))
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
                .group(1, g -> g
                        .section(0, parent)
                        .section(1, parent))
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
                .group(1, g -> g
                        .section(0, parent)
                        .section(1, parent))
                .group(2, g -> g
                        .section(0, parent)
                        .section(1, parent))
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
    public void bookAwardType() {
        Book classUnderTest = new BookBuilder(1)
                .award()
                .group(1, g -> g
                        .section(0, parent))
                .build();
        Award award = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getAwards().iterator().next();
        assertEquals(book, award.getAccomplishmentLevel());
    }

    @Test
    public void bookAwardName() {
        Book classUnderTest = new BookBuilder(1)
                .award(r -> r.name("Award Name"))
                .group(1, g -> g
                        .section(0, parent))
                .build();
        Award award = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getAwards().iterator().next();
        assertEquals("Award Name", award.getName());
        assertEquals(1, award.list().size());
        assertEquals("Award Name", award.selectAwarded().getName());
    }

    @Test
    public void bookName() {
        Book classUnderTest = new BookBuilder(1)
                .name("Book One")
                .award()
                .group(1, g -> g
                        .section(0, parent))
                .build();
        assertEquals("Book One", classUnderTest.getName());
        Award award = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getAwards().iterator().next();
        assertEquals("Book One", award.getName());
        assertEquals(1, award.list().size());
        assertEquals("Book One", award.selectAwarded().getName());
    }

    @Test
    public void bookAwardDefaultName() {
        Book classUnderTest = new BookBuilder(1)
                .award()
                .group(1, g -> g
                        .section(0, parent))
                .build();

        assertEquals("1", classUnderTest.getName());
        Award award = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getAwards().iterator().next();
        assertEquals("1", award.getName());
        assertEquals(1, award.list().size());
        assertEquals("1", award.selectAwarded().getName());
    }

    @Test
    public void bookwardWithName() {
        Book classUnderTest = new BookBuilder(1)
                .award(r -> r.name("Award Name"))
                .group(1, g -> g
                        .section(0, parent))
                .build();

        assertEquals("1", classUnderTest.getName());
        assertEquals("Award Name",
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getAwards().iterator().next().getName());
    }

    @Test
    public void bookAwardInCurriculumWithName() {
        Book classUnderTest = new CurriculumBuilder().book(0, b -> b
                .award(r -> r.name("Award Name"))
                .group(1, g -> g
                        .section(0, parent)))
                .build().getBooks().get(0);

        assertEquals("0", classUnderTest.getName());
        assertEquals("Award Name",
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getAwards().iterator().next().getName());
    }

    @Test
    public void bookAwardSequence() {
        Book classUnderTest = new CurriculumBuilder().book(0, b -> b
                .award(r -> r
                        .sequence(s -> s.item(i->i.name("Award Name 1"))
                                        .item(i->i.name("Award Name 2"))
                                        .item(i->i.name("Award Name 3")))
                )
                .group(1, g -> g
                        .section(0, parent)))
                .build().getBooks().get(0);

        assertEquals("0", classUnderTest.getName());
        Award award = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getAwards().iterator().next();
        assertEquals("0", award.getName());
        assertEquals(3, award.list().size());
        assertEquals("Award Name 1", award.selectAwarded().getName());
        assertEquals("Award Name 2", award.selectAwarded(c->c.getName().equals("Award Name 2")).getName());

    }

    @Test
    public void forEachXTest() {
        Book classUnderTest = new CurriculumBuilder().book(0, b -> b
                .award(r -> r
                        .forEach(4).name("badge")
                )
                .group(1, g -> g
                        .section(0, regular)
                        .section(1, regular)
                        .section(2, regular)
                        .section(3, regular)
                        .section(4, regular)
                        .section(5, regular))
                .group(1, g -> g
                        .section(0, regular)
                        .section(1, regular)
                        .section(2, regular)
                        .section(3, regular)
                        .section(4, regular)
                        .section(5, regular)))
                .build().getBooks().get(0);

        Award award = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getAwards().iterator().next();
        assertEquals("badge", award.getName());
        assertEquals(3, award.list().size());
        assertEquals("badge 1", award.selectAwarded().getName());
        assertEquals("badge 3", award.selectAwarded(c->c.getName().equals("badge 3")).getName());

    }

    @Test
    public void multipleBookAwards() {
        Book classUnderTest = new BookBuilder(1)
                .award(r -> r.name("Award Name"))
                .award()
                .name("Book 1")
                .group(1, g -> g
                        .section(0, parent))
                .build();

        assertEquals("Book 1", classUnderTest.getName());
        Set<Award> awards = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getAwards();
        assertEquals(2, awards.size());
        Iterator<Award> iterator = awards.iterator();
        assertEquals("Award Name",
                iterator.next().getName());
        assertEquals("Book 1",
                iterator.next().getName());
    }

    @Test
    public void ageGroups() {
        Book classUnderTest = new BookBuilder(1)
                .ageGroup(AgeGroup.DefaultAgeGroup.COLLEGE)
                .ageGroup(AgeGroup.DefaultAgeGroup.ELEVENTH_GRADE)
                .build();
        assertEquals(Arrays.asList(AgeGroup.DefaultAgeGroup.COLLEGE, AgeGroup.DefaultAgeGroup.ELEVENTH_GRADE),
                classUnderTest.getAgeGroups());
    }

    @Test
    public void setBookVersion() {
        Book classUnderTest = new BookBuilder(4)
                .version(1, 0)
                .build();
        assertEquals(1, classUnderTest.getVersion().major());
        assertEquals(0, classUnderTest.getVersion().minor());
    }

    @Test
    public void setId() {
        assertEquals("foobar", new BookBuilder(1).shortCode("foobar").build().getShortCode());
    }

    @Test
    public void translation() {
        Translation translation = new Translation() {
        };
        assertEquals(translation, new BookBuilder(3).translation(translation).build().getVersion().getTranslation());
    }

    @Test
    public void noTranslation() {
        assertEquals(Translation.none, new BookBuilder(3).build().getVersion().getTranslation());
    }

    @Test
    public void language() {
        assertEquals(Locale.FRENCH, new BookBuilder(3).language(Locale.FRENCH).build().getVersion().getLanguage());
    }

    @Test
    public void noLanguage() {
        assertEquals(Translation.none, new BookBuilder(3).build().getVersion().getTranslation());
    }
}