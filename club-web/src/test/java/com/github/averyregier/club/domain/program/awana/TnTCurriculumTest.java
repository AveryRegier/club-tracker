package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.*;
import org.junit.Test;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TnTCurriculumTest {
    Curriculum classUnderTest = TnTCurriculum.get();

    @Test
    public void thirdGradeBookRecommendation() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.THIRD_GRADE);
        assertNotNull(recommendation);
        assertTrue(recommendation.size()>=1);
        assertSame(0, recommendation.get(0).sequence());
    }

    @Test
    public void allBooksHaveProperID() {
        for(Book book: classUnderTest.getBooks()) {
            assertEquals(
                    book.getContainer().getId()+":"+book.getShortCode()+book.getVersion(),
                    book.getId());
        }
    }

    @Test
    public void curriculumID() {
        assertEquals("TnT", classUnderTest.getId());
        assertEquals("TnT", classUnderTest.getShortCode());
    }

    @Test
    public void ultimateAdventure() {
        Curriculum ua = classUnderTest.getSeries().get(0);
        assertNotNull(ua);
        assertEquals("UA", ua.getShortCode());
        assertEquals(classUnderTest, ua.getContainer());
        assertTrue(ua.getSeries().isEmpty());
    }

    @Test
    public void allCurriculumHaveProperID() {
        for(Curriculum book: classUnderTest.getSeries()) {
            assertEquals(
                    book.getContainer().getId()+":"+book.getShortCode(),
                    book.getId());
        }
    }

    @Test
    public void ultimateChallenge() {
        Curriculum uc = classUnderTest.getSeries().get(1);
        assertNotNull(uc);
        assertEquals("UC", uc.getShortCode());
        assertEquals(classUnderTest, uc.getContainer());
        assertTrue(uc.getSeries().isEmpty());
    }

    @Test
    public void uaStartZone() {
        Curriculum ua = classUnderTest.getSeries().get(0);
        Book sz = ua.getBooks().get(0);
        assertEquals("SZ", sz.getShortCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.THIRD_GRADE,
                AgeGroup.DefaultAgeGroup.FOURTH_GRADE), sz.getAgeGroups());
        assertEquals(Year.of(2010), sz.getVersion().getPublicationYear());
        assertEquals("©2010", sz.getVersion().toString());
        assertEquals(0, sz.sequence());
        assertEquals("Ultimate Adventure Start Zone", sz.getName());
        assertEquals("T&T Ultimate Adventure Uniform",
                sz.getSections().get(0).getRewards(RewardType.group).iterator().next().getName());

        assertStartZoneSections(sz);
    }

    private void assertStartZoneSections(Book sz) {
        assertEquals(7, sz.getSections().size());
        int number = 0;
        for(Section s: sz.getSections()) {
            assertEquals(Integer.toString(++number), s.getShortCode());
            assertEquals(1, s.getRewards(RewardType.group).size());
            assertEquals(regular, s.getSectionType());
            assertEquals(number, s.sequence());
        }
    }

    @Test
    public void uaBookOne() {
        Curriculum ua = classUnderTest.getSeries().get(0);
        Book book = ua.getBooks().get(1);
        assertEquals("1", book.getShortCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.THIRD_GRADE,
                AgeGroup.DefaultAgeGroup.FOURTH_GRADE), book.getAgeGroups());
        assertEquals(Year.of(2010), book.getVersion().getPublicationYear());
        assertEquals("©2010", book.getVersion().toString());
        assertEquals(1, book.sequence());
        assertEquals("Ultimate Adventure Book 1", book.getName());
        assertEquals("T&T Alpha Award",
                book.getSections().get(0).getRewards(RewardType.book).iterator().next().getName());

        assertNormalTnTStructure(book);
    }

    @Test
    public void uaBookTwo() {
        Curriculum ua = classUnderTest.getSeries().get(0);
        Book book = ua.getBooks().get(2);
        assertEquals("2", book.getShortCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.FOURTH_GRADE), book.getAgeGroups());
        assertEquals(Year.of(2010), book.getVersion().getPublicationYear());
        assertEquals("©2010", book.getVersion().toString());
        assertEquals(2, book.sequence());
        assertEquals("Ultimate Adventure Book 2", book.getName());

        assertNormalTnTStructure(book);
    }

    @Test
    public void ucStartZone() {
        Curriculum ua = classUnderTest.getSeries().get(1);
        Book sz = ua.getBooks().get(0);
        assertEquals("SZ", sz.getShortCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.FIFTH_GRADE,
                AgeGroup.DefaultAgeGroup.SIXTH_GRADE), sz.getAgeGroups());
        assertEquals(Year.of(2010), sz.getVersion().getPublicationYear());
        assertEquals("©2010", sz.getVersion().toString());
        assertEquals(0, sz.sequence());
        assertEquals("Ultimate Challenge Start Zone", sz.getName());
        assertEquals("T&T Ultimate Challenge Uniform",
                sz.getSections().get(0).getRewards(RewardType.group).iterator().next().getName());

        assertStartZoneSections(sz);
    }

    @Test
    public void ucBookOne() {
        Curriculum ua = classUnderTest.getSeries().get(1);
        Book book = ua.getBooks().get(1);
        assertEquals("1", book.getShortCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.FIFTH_GRADE,
                AgeGroup.DefaultAgeGroup.SIXTH_GRADE), book.getAgeGroups());
        assertEquals(Year.of(2010), book.getVersion().getPublicationYear());
        assertEquals("©2010", book.getVersion().toString());
        assertEquals(1, book.sequence());
        assertEquals("Ultimate Challenge Book 1", book.getName());

        assertNormalTnTStructure(book);
    }

    @Test
    public void ucBookTwo() {
        Curriculum ua = classUnderTest.getSeries().get(1);
        Book book = ua.getBooks().get(2);
        assertEquals("2", book.getShortCode());
        assertEquals(Arrays.asList(
                AgeGroup.DefaultAgeGroup.SIXTH_GRADE), book.getAgeGroups());
        assertEquals(Year.of(2010), book.getVersion().getPublicationYear());
        assertEquals("©2010", book.getVersion().toString());
        assertEquals(2, book.sequence());
        assertEquals("Ultimate Challenge Book 2", book.getName());

        assertNormalTnTStructure(book);
    }

    private void assertNormalTnTStructure(Book book1) {
        assertEquals(8, book1.getSectionGroups().size());
        int number=0;
        for(SectionGroup group: book1.getSectionGroups()) {
            assertEquals(Integer.toString(++number), group.getShortCode());
            assertEquals(number, group.sequence());
            assertEquals("Discovery "+number, group.getName());
            // every discovery starts with a parent section
            assertParentSections(group);

            // Every Discovery has seven more 'regular' sections that must also be passed
            int sectionNumber = assertRegularSections(group);

            // Every Discovery has three extra credit sections, one silver and two gold
            assertExtraCreditSections(group, sectionNumber);
        }

        // kids get 1 extra credit reward for each two discoveries worth of extra credit sections for each color
        assertCorrectExtraCreditRewards(book1,
                s -> s.getShortCode().equals("S"),
                "Silver 1", "Silver 2", "Silver 3", "Silver 4");

        assertCorrectExtraCreditRewards(book1,
                s -> s.getShortCode().startsWith("G"),
                "Gold 1", "Gold 2", "Gold 3", "Gold 4");
    }

    private List<Reward> assertCorrectExtraCreditRewards(Book book1, Predicate<Section> fn, String... names) {
        List<Section> extraCreditSections = book1.getSections().stream()
                .filter(fn)
                .collect(Collectors.toList());
        List<Reward> silverRewards = extraCreditSections.stream()
                .flatMap(s -> s.getRewards(RewardType.group).stream())
                .distinct()
                .collect(Collectors.toList());
        assertEquals(names.length, silverRewards.size());
        assertEquals(Arrays.asList(names),
                     silverRewards.stream()
                             .map(s->s.getName())
                             .collect(Collectors.toList()));

        int number = 0;
        for(Reward reward: silverRewards) {
            assertEquals(extraCreditSections.size()/names.length, reward.getSections().size());
            List<SectionGroup> discoveries = reward.getSections().stream()
                    .map(s -> s.getGroup())
                    .distinct()
                    .collect(Collectors.toList());
            assertEquals(2, discoveries.size());
            for(SectionGroup discovery: discoveries) {
                assertEquals(++number, discovery.sequence());
            }
        }
        return silverRewards;
    }

    private void assertParentSections(SectionGroup group) {
        Section parentSection = group.getSections().get(0);
        assertEquals(parent, parentSection.getSectionType());
        assertEquals(0, parentSection.sequence());
        assertEquals("0", parentSection.getShortCode());
        assertEquals(1, parentSection.getRewards(RewardType.group).size());
        assertFalse(parentSection.getRewards(RewardType.book).isEmpty());
    }

    private int assertRegularSections(SectionGroup group) {
        List<Section> regular = group.getSections().stream()
                .filter(s -> s.getSectionType() != extaCredit &&
                        s.getSectionType() != parent)
                .collect(Collectors.toList());

        assertEquals(7, regular.size());
        int sectionNumber = 0;
        for(Section s: regular) {
            assertEquals(++sectionNumber, s.sequence());
            assertEquals(Integer.toString(sectionNumber), s.getShortCode());
            assertTrue(s.getSectionType().mustBeSigned());
            assertTrue(s.getSectionType().requiredForGroupReward());
            assertTrue(s.getSectionType().requiredForBookReward());
            assertTrue(s.getSectionType().countsTowardsSectionMinimums());
            assertEquals(group, s.getGroup());
            Set<Reward> groupRewards = s.getRewards(RewardType.group);
            assertEquals(1, groupRewards.size());
            assertEquals(group.getName(), groupRewards.iterator().next().getName());
            assertFalse(s.getRewards(RewardType.book).isEmpty());
        }
        return sectionNumber;
    }

    private void assertExtraCreditSections(SectionGroup group, int sectionNumber) {
        List<Section> extraCredit = group.getSections().stream()
                .filter(s -> s.getSectionType() == extaCredit)
                .collect(Collectors.toList());

        assertEquals(3, extraCredit.size());
        String[] shortCodes = {"S", "G1", "G2"};
        for(Section s: extraCredit) {
            assertEquals(++sectionNumber, s.sequence());
            assertEquals(shortCodes[sectionNumber-8], s.getShortCode());
            assertTrue(s.getSectionType().mustBeSigned());
            assertTrue(s.getSectionType().requiredForGroupReward());
            assertFalse(s.getSectionType().requiredForBookReward());
            assertTrue(s.getSectionType().countsTowardsSectionMinimums());
            assertEquals(group, s.getGroup());
            assertEquals(1, s.getRewards(RewardType.group).size());
            assertEquals(0, s.getRewards(RewardType.book).size());
        }
    }

    @Test
    public void specialSections() {
        assertEquals(friend, classUnderTest.lookup(classUnderTest.getId()+":UA:1©2010:5:7").get().getSectionType());
        assertEquals(group, classUnderTest.lookup(classUnderTest.getId()+":UA:2©2010:4:3").get().getSectionType());
        assertEquals(friend, classUnderTest.lookup(classUnderTest.getId()+":UA:2©2010:6:5").get().getSectionType());
        assertEquals(friend, classUnderTest.lookup(classUnderTest.getId()+":UC:1©2010:1:7").get().getSectionType());
        assertEquals(friend, classUnderTest.lookup(classUnderTest.getId()+":UC:2©2010:1:7").get().getSectionType());
    }
}
