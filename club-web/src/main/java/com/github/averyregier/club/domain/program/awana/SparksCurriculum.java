package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionTypeDecider;

import java.util.function.UnaryOperator;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.*;
import static com.github.averyregier.club.domain.program.awana.SparksSectionTypes.*;
import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.regular;

/**
 * Created by avery on 9/12/2014.
 */
public class SparksCurriculum {
    // TODO: Blue Jewel attendance awards

    public static final Curriculum curriculum = build(new CurriculumBuilder()).build();

    public static Curriculum get() {
        return curriculum;
    }

    public static CurriculumBuilder build(CurriculumBuilder builder) {
        return builder
                .shortCode("Sparks")
                .book(0, flight316())
                .book(1, book1())
                .book(2, frequentFlyer1())
                .book(3, book2())
                .book(4, frequentFlyer2())
                .book(4, book3())
                .book(5, frequentFlyer3());
    }

    private static UnaryOperator<BookBuilder> flight316() {
        return b -> b
                .shortCode("F316")
                .name("Flight 3:16")
                .ageGroup(KINDERGARTEN)
                .ageGroup(FIRST_GRADE)
                .ageGroup(SECOND_GRADE)
                .publicationYear(2010)
                .group(0, g ->
                        g.name("Flight 3:16")
                         .award(r -> r
                                .name("Sparks Vest")
                                .section(1, regular)
                                .section(2, regular)
                                .section(3, regular)
                                .section(4, regular)
                                .section(5, regular)
                                .section(6, regular)));
    }

    private static UnaryOperator<BookBuilder> book1() {
        return b -> {
            b.shortCode("1")
             .name("Hang Glider")
             .ageGroup(KINDERGARTEN)
             .ageGroup(FIRST_GRADE)
             .ageGroup(SECOND_GRADE)
             .publicationYear(2010)
             .award(r -> r.name("First Book Ribbon"));
            return sparksStructure("Hang Glider", b);
        };
    }

    private static UnaryOperator<BookBuilder> frequentFlyer1() {
        return b -> {
            b.shortCode("FF1")
             .ageGroup(KINDERGARTEN);
            return frequentFlyerStructure("Hang Glider", b);
        };
    }

    private static UnaryOperator<BookBuilder> book2() {
        return b -> {
            b.shortCode("2")
             .name("Wing Runner")
             .ageGroup(FIRST_GRADE)
             .ageGroup(SECOND_GRADE)
             .publicationYear(2010)
                    .award(r -> r.name("Second Book Ribbon"));
            return sparksStructure("Wing Runner", b);
        };
    }

    private static UnaryOperator<BookBuilder> frequentFlyer2() {
        return b -> {
            b.shortCode("FF2")
             .ageGroup(FIRST_GRADE);
            return frequentFlyerStructure("Wing Runner", b);
        };
    }

    private static UnaryOperator<BookBuilder> book3() {
        return b -> {
            b.shortCode("3")
             .name("Sky Stormer")
             .ageGroup(SECOND_GRADE)
             .publicationYear(2010)
                    .award(r -> r.name("Sparky Award"));
            return sparksStructure("Sky Stormer", b);
        };
    }

    private static UnaryOperator<BookBuilder> frequentFlyer3() {
        return b -> {
            b.shortCode("FF3")
             .ageGroup(SECOND_GRADE);
            return frequentFlyerStructure("Sky Stormer", b);
        };
    }

    private static SectionTypeDecider getSparksTypeAssigner() {
        return (g, s) -> {
            if (g == 1 && s == 1) return friend;
            else if(g==9) return review;
            else return regular;
        };
    }

    private static BookBuilder sparksStructure(String name, BookBuilder builder) {
        return builder
                .typeAssigner(getSparksTypeAssigner())
                .award()
                .group(0, rankPatch(name + " Rank Patch", "RP"))
                .group(1, jewel("Red Jewel 1", "RJ1"))
                .group(2, jewel("Green Jewel 1", "GJ1"))
                .group(3, jewel("Red Jewel 2", "RJ2"))
                .group(4, jewel("Green Jewel 2", "GJ2"))
                .group(5, jewel("Red Jewel 3", "RJ3"))
                .group(6, jewel("Green Jewel 3", "GJ3"))
                .group(7, jewel("Red Jewel 4", "RJ4"))
                .group(8, jewel("Green Jewel 4", "GJ4"))
                .group(9, review(name));
    }

    private static UnaryOperator<SectionGroupBuilder> rankPatch(String name, String id) {
        return g -> g
                .name(name)
                .shortCode(id)
                .award(r -> r
                        //.name(name)
                        .section(1)
                        .section(2)
                        .section(3)
                        .section(4)
                        .section(5)
                        .section(6)
                        .section(7)
                        .section(8));
    }

    private static UnaryOperator<SectionGroupBuilder> jewel(String name, String id) {
        return g -> g
                .name(name)
                .shortCode(id)
                .award(r -> r
                        .name(name)
                        .section(1)
                        .section(2)
                        .section(3)
                        .section(4));
    }

    private static BookBuilder frequentFlyerStructure(String name, BookBuilder builder) {
        return builder
                .name(name+" Frequent Flyer")
                .publicationYear(2008)
                .award(r -> r.name(name+" Extra Credit Pin"))
                .typeAssigner((g, s) -> extraCredit)
                .group(1, extraCredit("Takeoff", "TO"))
                .group(2, extraCredit("Passport", "PP"))
                .group(3, extraCredit("Passenger List", "PL"))
                .group(4, extraCredit("Landing Gear", "LG"));
    }

    private static UnaryOperator<SectionGroupBuilder> review(String name) {
        return g -> g
                .name(name+" Review")
                .shortCode("R")
                .award(r -> r
                        .name(name + " Review Emblem")
                        .section(1)
                        .section(2)
                        .section(3)
                        .section(4)
                        .section(5)
                        .section(6)
                        .section(7)
                        .section(8)
                        .section(9)
                        .section(10)
                        .section(11)
                        .section(12)
                        .section(13)
                        .section(14)
                        .section(15));
    }

    private static UnaryOperator<SectionGroupBuilder> extraCredit(String name, String id) {
        return g -> g
                .name(name)
                .shortCode(id)
                .section(1)
                .section(2)
                .section(3)
                .section(4)
                .section(5);
    }
}
