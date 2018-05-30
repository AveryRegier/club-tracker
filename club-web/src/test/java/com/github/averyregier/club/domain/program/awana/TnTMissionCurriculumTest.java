package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.*;
import org.junit.Test;

import java.time.Year;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.program.AccomplishmentLevel.group;
import static com.github.averyregier.club.domain.program.awana.TnTMissionSectionTypes.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class TnTMissionCurriculumTest {
    Curriculum classUnderTest = TnTMissionCurriculum.get();

    @Test
    public void seventhGradeGetsNoBooks() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.SEVENTH_GRADE);
        assertNotNull(recommendation);
        assertEquals(0, recommendation.size());
    }

    @Test
    public void secondGradeGetsNoBooks() {
        List<Book> recommendation = classUnderTest.recommendedBookList(AgeGroup.DefaultAgeGroup.SECOND_GRADE);
        assertNotNull(recommendation);
        assertEquals(0, recommendation.size());
    }

    @Test
    public void allBooksHaveProperID() {
        for (Book book : classUnderTest.getBooks()) {
            assertEquals(
                    book.getContainer().getId() + ":" + book.getShortCode() + book.getVersion(),
                    book.getId());
        }
    }

    @Test
    public void curriculumID() {
        assertEquals("TnT", classUnderTest.getId());
        assertEquals("TnT", classUnderTest.getShortCode());
    }

    @Test
    public void correctSectionName() {
        Section firstSection = classUnderTest.getBooks().get(1).getSections().get(0);
        assertEquals("GOD IS CREATOR", firstSection.getSectionTitle());
    }

    @Test
    public void missionStartZone() {
        for (int i = 0; i < classUnderTest.getBooks().size(); i += 2) {
            assertStartZone(classUnderTest.getBooks().get(i));
        }
    }

    private void assertStartZone(Book sz) {
        assertEquals("MSZ", sz.getShortCode());
        assertEquals("MSZ", sz.getMwhCode());
//        assertEquals(Arrays.asList(
//                AgeGroup.DefaultAgeGroup.THIRD_GRADE,
//                AgeGroup.DefaultAgeGroup.FOURTH_GRADE), sz.getAgeGroups());
        assertEquals(Year.of(2016), sz.getVersion().getPublicationYear());
        assertEquals("c2016", sz.getVersion().toString());
        assertEquals(0, sz.sequence() % 2);
        assertEquals("Mission Start Zone", sz.getName());
        assertEquals("T&T Ultimate Adventure Uniform",
                sz.getSections().get(0).getAwards(group).iterator().next().getName());

        assertStartZoneSections(sz);
    }

    private void assertStartZoneSections(Book sz) {
        assertEquals(2, sz.getSections().size());
        int number = 0;
        for (Section s : sz.getSections()) {
            assertEquals(Integer.toString(++number), s.getShortCode());
            assertEquals(1, s.getAwards(group).size());
            assertEquals(regular, s.getSectionType());
            assertEquals(number, s.sequence());
        }
    }

    @Test
    public void regularSections() {
        for (Book book : classUnderTest.getBooks()) {
            if (isNotStartZone(book)) {
                assertThat(getSectionsThatMatch(book,
                        s -> s.getSectionType().requiredFor(AccomplishmentLevel.book)).count(), is(30L));
                getSectionsOfType(book, regular)
                        .forEach(s -> assertThat(s.getShortCode(), is(Integer.toString((s.sequence() - 1) / 3 + 1))));
            }
        }
    }

    @Test
    public void regularSectionTypeAttributes() {
        assertThat(regular.getCssClass(), is("regular"));
        assertThat(regular.getReadableName(), is("Regular"));
        assertThat(regular.toString(), is("regular"));
        assertThat(regular.requiredToMoveOn(), is(false));
        assertThat(regular.mustBeSigned(), is(true));
        assertThat(regular.requiredFor(AccomplishmentLevel.book), is(true));
        assertThat(regular.requiredFor(group), is(true));
        assertThat(regular.isExtraCredit(), is(false));
    }

    @Test
    public void goSections() {
        List<Section> goSections = getSectionsOfType(go);
        assertSectionsOnFirstBook(goSections);
        assertThat(goSections.size(), is(3));
        assertThat(streamGroupSequences(goSections).distinct().count(), is(3L));
        assertThat(streamGroupSequences(goSections).distinct().min().orElse(-1), is(1));
        assertThat(streamGroupSequences(goSections).distinct().max().orElse(Integer.MAX_VALUE), is(3));
    }

    @Test
    public void goSectionTypeAttributes() {
        assertThat(go.getCssClass(), is("go"));
        assertThat(go.getReadableName(), is("Go"));
        assertThat(go.toString(), is("go"));
        assertThat(go.requiredToMoveOn(), is(false));
        assertThat(go.mustBeSigned(), is(true));
        assertThat(go.requiredFor(AccomplishmentLevel.book), is(true));
        assertThat(go.requiredFor(group), is(true));
        assertThat(go.isExtraCredit(), is(false));
    }

    private IntStream streamGroupSequences(List<Section> goSections) {
        return goSections.stream().map(Section::getGroup).mapToInt(SectionGroup::sequence);
    }

    private void assertSectionsOnFirstBook(List<Section> goSections) {
        List<Book> books = goSections.stream().map(Section::getGroup)
                .map(SectionHolder::getBook)
                .distinct()
                .collect(Collectors.toList());
        assertThat(books.size(), is(1));
        assertThat(books.get(0).sequence(), is(1));
    }

    private List<Section> getSectionsOfType(SectionType sectionType) {
        return classUnderTest.getBooks().stream()
                .flatMap(b -> getSectionsOfType(b, sectionType))
                .collect(Collectors.toList());
    }

    @Test
    public void reviewSections() {
        List<Section> reviewSections = getSectionsOfType(review);
        assertThat(reviewSections.size(), is(12));
        assertThat(streamGroupSequences(reviewSections).boxed().collect(Collectors.toList()),
                is(IntStream.of(1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4).boxed().collect(Collectors.toList())));

        for (Book book : classUnderTest.getBooks()) {
            if (isNotStartZone(book)) {
                for (SectionGroup group : book.getSectionGroups()) {
                    assertThat(getSectionsOfType(group, review).count(), is(1L));
                    assertReviewAtEndOfGroup(group);
                }
            }
        }
    }

    private void assertReviewAtEndOfGroup(SectionGroup group) {
        OptionalInt section = getSectionsOfType(group, review)
                .mapToInt(Section::sequence)
                .findFirst();
        assertThat(section.getAsInt(),
                is(group.getSections().stream().filter(s -> s.getSectionType() != go)
                        .mapToInt(Section::sequence)
                        .max()
                        .getAsInt()));
    }

    private Stream<Section> getSectionsOfType(SectionHolder holder, SectionType sectionType) {
        return holder.getSections().stream().filter(s -> s.getSectionType() == sectionType);
    }

    private Stream<Section> getSectionsThatMatch(SectionHolder holder, Predicate<Section> test) {
        return holder.getSections().stream().filter(test);
    }

    @Test
    public void reviewSectionTypeAttributes() {
        assertThat(review.getCssClass(), is("review"));
        assertThat(review.getReadableName(), is("Review"));
        assertThat(review.toString(), is("review"));
        assertThat(review.requiredToMoveOn(), is(false));
        assertThat(review.mustBeSigned(), is(true));
        assertThat(review.requiredFor(AccomplishmentLevel.book), is(true));
        assertThat(review.requiredFor(group), is(true));
        assertThat(review.isExtraCredit(), is(false));
    }

    @Test
    public void extraCreditSectionsShowUpProperly() {
        classUnderTest.getBooks().stream()
                .filter(this::isNotStartZone)
                .flatMap(b -> b.getSectionGroups().stream())
                .forEach(group -> {
                    assertExtraCreditSections(group, silver, "S");
                    assertExtraCreditSections(group, gold, "G");
                });

        assertExtraCreditSectionsInOrder();
    }

    @Test
    public void fourSilverAndGoldInEachBook() {
        classUnderTest.getBooks().stream().filter(this::isNotStartZone)
                .forEach(book -> {
                    assertThat(getAwardCount(book, silver),
                            is(4L));
                    assertThat(getAwardCount(book, gold),
                            is(4L));
                });
    }

    private long getAwardCount(SectionHolder holder, TnTMissionSectionTypes silver) {
        return getSectionsOfType(holder, silver).flatMap(s -> s.getAwards().stream()).distinct().count();
    }

    private boolean isNotStartZone(Book b) {
        return !b.getShortCode().contains("SZ");
    }

    private void assertExtraCreditSectionsInOrder() {
        Section previous = null;
        for (Book book : classUnderTest.getBooks()) {
            if (isNotStartZone(book)) {
                for (Section section : book.getSections()) {
                    if (previous == null) {
                    } else if (previous.getSectionType() == regular) {
                        assertThat(section.getSectionType(), is(silver));
                        assertThat(section.getShortCode(), is(previous.getShortCode() + "S"));
                    } else if (previous.getSectionType() == silver) {
                        assertThat(section.getSectionType(), is(gold));
                        assertThat(section.getShortCode(),
                                is(previous.getShortCode().substring(0, previous.getShortCode().length() - 1) + "G"));
                    }
                    previous = section;
                }
            }
        }
    }

    private void assertExtraCreditSections(SectionGroup group, TnTMissionSectionTypes sectionType, String suffix) {
        getSectionsOfType(group, sectionType)
                .forEach(s -> {
                    assertThat(s.getShortCode(), endsWith(suffix));
                    assertThat(s.getSectionType(), equalTo(sectionType));
                    assertTrue(s.getSectionType().mustBeSigned());
                    assertTrue(s.getSectionType().requiredFor(AccomplishmentLevel.group));
                    assertFalse(s.getSectionType().requiredFor(AccomplishmentLevel.book));
                    assertTrue(s.getSectionType().countsTowardsSectionMinimums());
                    assertEquals(group, s.getGroup());
                    assertEquals(1, s.getAwards(AccomplishmentLevel.group).size());
                    assertEquals(0, s.getAwards(AccomplishmentLevel.book).size());
                });
        assertThat(getAwardCount(group, sectionType), is(1L));
    }
}