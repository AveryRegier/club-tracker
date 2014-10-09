package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.*;
import org.junit.Test;

import java.util.*;

import static com.github.averyregier.club.domain.program.RewardType.book;
import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.parent;
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
    public void bookRewardType() {
        Book classUnderTest = new BookBuilder(1)
                .reward()
                .group(1, g -> g
                        .section(0, parent))
                .build();
        assertEquals(book,
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getRewardType());
    }

    @Test
    public void bookRewardName() {
        Book classUnderTest = new BookBuilder(1)
                .reward(r->r.name("Reward Name"))
                .group(1, g -> g
                        .section(0, parent))
                .build();
        assertEquals("Reward Name",
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getName());
    }

    @Test
    public void bookName() {
        Book classUnderTest = new BookBuilder(1)
                .name("Book One")
                .reward()
                .group(1, g -> g
                        .section(0, parent))
                .build();
        assertEquals("Book One", classUnderTest.getName());
        assertEquals("Book One",
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getName());
    }

    @Test
    public void bookRewardDefaultName() {
        Book classUnderTest = new BookBuilder(1)
                .reward()
                .group(1, g -> g
                        .section(0, parent))
                .build();

        assertEquals("1", classUnderTest.getName());
        assertEquals("1",
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getName());
    }

    @Test
    public void bookRewardWithName() {
        Book classUnderTest = new BookBuilder(1)
                .reward(r->r.name("Reward Name"))
                .group(1, g -> g
                        .section(0, parent))
                .build();

        assertEquals("1", classUnderTest.getName());
        assertEquals("Reward Name",
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getName());
    }

    @Test
    public void bookRewardInCurriculumWithName() {
        Book classUnderTest = new CurriculumBuilder().book(0, b -> b
                .reward(r -> r.name("Reward Name"))
                .group(1, g -> g
                        .section(0, parent)))
                .build().getBooks().get(0);

        assertEquals("0", classUnderTest.getName());
        assertEquals("Reward Name",
                classUnderTest.getSectionGroups().get(0).getSections().get(0)
                        .getRewards().iterator().next().getName());
    }

    @Test
    public void multipleBookRewards() {
        Book classUnderTest = new BookBuilder(1)
                .reward(r->r.name("Reward Name"))
                .reward()
                .name("Book 1")
                .group(1, g -> g
                        .section(0, parent))
                .build();

        assertEquals("Book 1", classUnderTest.getName());
        Set<Reward> rewards = classUnderTest.getSectionGroups().get(0).getSections().get(0)
                .getRewards();
        assertEquals(2, rewards.size());
        Iterator<Reward> iterator = rewards.iterator();
        assertEquals("Reward Name",
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