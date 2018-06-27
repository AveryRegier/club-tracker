package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.*;

import java.util.function.UnaryOperator;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.*;
import static com.github.averyregier.club.domain.program.awana.TnTMissionSectionTypes.start;

/**
 * Created by avery on 8/7/16.
 */
public class TnTMissionCurriculum {

    public static final Curriculum curriculum = build(new CurriculumBuilder()).build();

    public static Curriculum get() {
        return curriculum;
    }

    public static CurriculumBuilder build(CurriculumBuilder builder) {
        return builder
                .shortCode("TnT")
                .curriculum(c -> c
                        .shortCode("UA")
                        .book(0, startZone())
                        .book(1, book1())
                        .book(2, startZone())
                        .book(3, book2())
                        .book(4, startZone())
                        .book(5, book3()));
    }

    private static UnaryOperator<BookBuilder> startZone() {
        return b -> b
                .shortCode("MSZ")
                .mwhCode("MSZ")
                .name("Mission Start Zone")
                .ageGroup(THIRD_GRADE)
                .ageGroup(FOURTH_GRADE)
                .ageGroup(FIFTH_GRADE)
                .ageGroup(SIXTH_GRADE)
                .publicationYear(2016)
                .catalog("34146")
                .group(0, g ->
                        g.name("Start Zone")
                                .award(r -> r
                                        .name("T&T Ultimate Adventure Uniform")
                                        .section(1, start, s->s.name("The Gospel"))
                                        .section(2, start, s->s.name("Bible Basics"))));
    }

    private static UnaryOperator<BookBuilder> book1() {
        return b -> {
            b.shortCode("1")
                    .mwhCode("M1")
                    .name("Mission: Grace in Action")
                    .ageGroup(THIRD_GRADE)
                    .ageGroup(FOURTH_GRADE)
                    .ageGroup(FIFTH_GRADE)
                    .ageGroup(SIXTH_GRADE)
                    .publicationYear(2016)
                    .award(r -> r.sequence(s->s
                            .item(i-> alpha(i))
                            .item(i-> excellence(i))));
            return missionB1Structure(b);
        };
    }

    private static UnaryOperator<BookBuilder> book2() {
        return b -> {
            b.shortCode("1")
                    .mwhCode("M2")
                    .name("Mission: Evidence of Grace")
                    .ageGroup(THIRD_GRADE)
                    .ageGroup(FOURTH_GRADE)
                    .ageGroup(FIFTH_GRADE)
                    .ageGroup(SIXTH_GRADE)
                    .publicationYear(2017)
                    .award(r -> r.sequence(s->s
                            .item(i-> alpha(i))
                            .item(i-> excellence(i))));
            return missionB2Structure(b);
        };
    }

    private static UnaryOperator<BookBuilder> book3() {
        return b -> {
            b.shortCode("1")
                    .mwhCode("M3")
                    .name("Mission: Agents of Grace")
                    .ageGroup(THIRD_GRADE)
                    .ageGroup(FOURTH_GRADE)
                    .ageGroup(FIFTH_GRADE)
                    .ageGroup(SIXTH_GRADE)
                    .publicationYear(2018)
                    .award(r -> r.sequence(s->s
                            .item(i-> alpha(i))
                            .item(i-> excellence(i))));
            return missionB3Structure(b);
        };
    }

    private static BookBuilder missionB1Structure(BookBuilder builder) {
        AwardBuilder discovery = createDiscoveries();
        return builder
                .group(1, b1unit1(discovery))
                .group(2, b1unit2(discovery))
                .group(3, b1unit3(discovery))
                .group(4, b1unit4(discovery));
    }

    private static AwardBuilder createDiscoveries() {
        return new AwardBuilder().name("Discovery")
                .forEach(4)
                .sequence(a->a.item(i->i.name("Discovery 1"))
                              .item(i->i.name("Discovery 2"))
                              .item(i->i.name("Discovery 3"))
                              .item(i->i.name("Discovery 4"))
                              .item(i->i.name("Discovery 5"))
                              .item(i->i.name("Discovery 6"))
                              .item(i->i.name("Discovery 7"))
                              .item(i->i.name("Discovery 8")));
    }

    private static UnaryOperator<SectionGroupBuilder> b1unit1(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("GOD IS ..."), discovery)
                .completeWeek("GOD IS CREATOR")
                .completeWeek("GOD IS HOLY")
                .completeWeek("GOD IS JUST")
                .completeWeek("GOD IS LOVE")
                .completeWeek("GOD IS ETERNAL")
                .review("GOD IS ... REVIEW")
                .go("GOD IS WITH YOU").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b1unit2(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("THE BIBLE"), discovery)
                .completeWeek("THE BIBLE IS TRUE AND WITHOUT ERROR")
                .completeWeek("THE BIBLE HELPS US KNOW GOD")
                .completeWeek("THE BIBLE SHOULD BE STUDIED CAREFULLY")
                .completeWeek("THE BIBLE TEACHES US ABOUT PEOPLE FROM THE PAST")
                .completeWeek("THE BIBLE TEACHES US HOW TO LIVE")
                .completeWeek("THE BIBLE IS POWERFUL AND ACTIVE")
                .review("THE BIBLE REVIEW")
                .go("THE BIBLE IS OUR GUIDE").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b1unit3(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("JESUS"), discovery)
                .completeWeek("JESUS IS FULLY GOD")
                .completeWeek("JESUS IS FULLY MAN")
                .completeWeek("JESUS IS SAVIOR")
                .completeWeek("JESUS CONQUERED DEATH")
                .completeWeek("JESUS WANTS US TO FOLLOW HIM")
                .completeWeek("JESUS SHOWED GRACE TO OTHERS")
                .review("JESUS REVIEW")
                .go("JESUS WANTS YOU TO SHARE THE GOSPEL").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b1unit4(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("LIVING BY GRACE"), discovery)
                .completeWeek("WHAT IS GRACE")
                .completeWeek("GRACE AND THE LAW")
                .completeWeek("GRACE TO OBEY")
                .completeWeek("GRACE TO FORGIVE")
                .completeWeek("GRACE AND PAUL")
                .completeWeek("GRACE IN ACTION")
                .review("LIVING BY GRACE REVIEW").parent();
    }

    private static BookBuilder missionB2Structure(BookBuilder builder) {
        AwardBuilder discovery = createDiscoveries();
        return builder
                .group(1, b2unit1(discovery))
                .group(2, b2unit2(discovery))
                .group(3, b2unit3(discovery))
                .group(4, b2unit4(discovery));
    }

    private static UnaryOperator<SectionGroupBuilder> b2unit1(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("GOD IS ..."), discovery)
                .completeWeek("GOD IS TRUTH")
                .completeWeek("GOD IS ALL-POWERFUL")
                .completeWeek("GOD IS EVERYWHERE")
                .completeWeek("GOD IS ALL-KNOWING")
                .completeWeek("GOD IS THREE IN ONE")
                .review("GOD IS ... REVIEW").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b2unit2(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("THE BIBLE"), discovery)
                .completeWeek("BOOKS OF HISTORY")
                .completeWeek("BOOKS OF WISDOM")
                .completeWeek("BOOKS OF PROPHECY")
                .completeWeek("THE GOSPELS")
                .completeWeek("ACTS")
                .completeWeek("THE EPISTLES")
                .completeWeek("REVELATIONS")
                .review("THE BIBLE REVIEW").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b2unit3(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("REDEMPTION"), discovery)
                .completeWeek("IMAGE OF GOD")
                .completeWeek("SATAN")
                .completeWeek("THE FALL")
                .completeWeek("SIN AND SUFFERING")
                .completeWeek("JESUS CHRIST")
                .completeWeek("THE LORD'S RETURN")
                .completeWeek("ETERNITY")
                .review("REDEMPTION REVIEW").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b2unit4(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("EVIDENCE"), discovery)
                .completeWeek("FAITH")
                .completeWeek("PRAYER")
                .completeWeek("STUDYING GOD'S WORD")
                .completeWeek("SERVING")
                .completeWeek("WORSHIP")
                .completeWeek("FELLOWSHIP")
                .completeWeek("WITNESSING")
                .review("EVIDENCE REVIEW").parent();
    }

    private static BookBuilder missionB3Structure(BookBuilder builder) {
        AwardBuilder discovery = createDiscoveries();
        return builder
                .group(1, b3unit1(discovery))
                .group(2, b3unit2(discovery))
                .group(3, b3unit3(discovery))
                .group(4, b3unit4(discovery));
    }

    private static UnaryOperator<SectionGroupBuilder> b3unit1(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("God Is ..."), discovery)
                .completeWeek("God Is Our Savior")
                .completeWeek("God Is Our Hope")
                .completeWeek("God Is Our Advocate")
                .completeWeek("God Is Our Strength")
                .completeWeek("God Is Our Peace")
                .review("Unit 1 Review").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b3unit2(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("The Bible"), discovery)
                .completeWeek("The Bible Is God’s Revelation")
                .completeWeek("The Bible Is God’s Inspired Word")
                .completeWeek("The Bible Lights Our Path")
                .completeWeek("The Bible Is True and Useful")
                .completeWeek("The Bible Is the Standard")
                .completeWeek("The Bible Is Trustworthy")
                .completeWeek("The Bible Is Helpful to Correct")
                .review("Unit 2 Review").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b3unit3(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("Jesus … I Am"), discovery)
                .completeWeek("I Am the Bread of Life ")
                .completeWeek("I Am the Light of the World")
                .completeWeek("I Am the Gate")
                .completeWeek("I Am the Good Shepherd")
                .completeWeek("I Am the Resurrection and the Life")
                .completeWeek("I Am the Way, the Truth, and the Life")
                .completeWeek("I Am the True Vine")
                .review("Unit 3 Review").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b3unit4(AwardBuilder discovery) {
        return g3 -> new MissionSectionBuilder(g3.name("Agents of ..."), discovery)
                .completeWeek("Agents of Courage")
                .completeWeek("Agents of Humility")
                .completeWeek("Agents of Wisdom")
                .completeWeek("Agents of Obedience")
                .completeWeek("Agents of Honor")
                .completeWeek("Agents of Hope")
                .completeWeek("Agents of Grace")
                .review("Unit 4 Review").parent();
    }

    private static CatalogueBuilder alpha(CatalogueBuilder i) {
        return i.name("T&T Alpha Award")
                .catalog("MV-TTAlphaAwardandPin", "Award and Pin")
                .catalog("79530", "Award")
                .catalog("79548", "Replacement Pin");
    }

    private static CatalogueBuilder excellence(CatalogueBuilder i) {
        return i.name("T&T Excellence Award")
                .catalog("79556", "Award and Pin")
                .catalog("79564", "Replacement Pin");
    }
}
