package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.CatalogueBuilder;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;

import java.util.function.UnaryOperator;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.*;
import static com.github.averyregier.club.domain.program.awana.TnTMissionSectionTypes.*;

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
                        .book(2, book2()));
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
                                        .section(1, regular).name("The Gospel")
                                        .section(2, regular).name("Bible Basics")));
    }

    private static UnaryOperator<BookBuilder> book1() {
        return b -> {
            b.shortCode("1")
                    .mwhCode("UAM1")
                    .name("Mission: Grace In Action")
                    .ageGroup(THIRD_GRADE)
                    .ageGroup(FOURTH_GRADE)
                    .ageGroup(FIFTH_GRADE)
                    .ageGroup(SIXTH_GRADE)
                    .publicationYear(2016)
                    .award(r -> r.sequence(s->s
                            .item(i-> alpha(i))
                            .item(i-> excellence(i))))
                    .typeAssigner((g, s) -> {
                        if (s == 0) return parent;
                        else if (s > 7) return extraCredit;
                        //else if (g == 5 && s == 7) return friend;
                        else return regular;
                    });
            return missionB1Structure(b);
        };
    }

    private static UnaryOperator<BookBuilder> book2() {
        return b -> {
            b.shortCode("1")
                    .mwhCode("UAM2")
                    .name("Mission: Evidence of Grace")
                    .ageGroup(THIRD_GRADE)
                    .ageGroup(FOURTH_GRADE)
                    .ageGroup(FIFTH_GRADE)
                    .ageGroup(SIXTH_GRADE)
                    .publicationYear(2017)
                    .award(r -> r.sequence(s->s
                            .item(i-> alpha(i))
                            .item(i-> excellence(i))))
                    .typeAssigner((g, s) -> {
                        if (s == 0) return parent;
                        else if (s > 7) return extraCredit;
                            //else if (g == 5 && s == 7) return friend;
                        else return regular;
                    });
            return missionB2Structure(b);
        };
    }

    private static UnaryOperator<BookBuilder> book3() {
        return b -> {
            b.shortCode("1")
                    .mwhCode("UAM3")
                    .name("Mission: Agents of Grace")
                    .ageGroup(THIRD_GRADE)
                    .ageGroup(FOURTH_GRADE)
                    .ageGroup(FIFTH_GRADE)
                    .ageGroup(SIXTH_GRADE)
                    .publicationYear(2018)
                    .award(r -> r.sequence(s->s
                            .item(i-> alpha(i))
                            .item(i-> excellence(i))))
                    .typeAssigner((g, s) -> {
                        if (s == 0) return parent;
                        else if (s > 7) return extraCredit;
                            //else if (g == 5 && s == 7) return friend;
                        else return regular;
                    });
            return b;
        };
    }

    private static BookBuilder missionB1Structure(BookBuilder builder) {
        return builder
                .group(1, b1group1())
                .group(2, b1group2())
                .group(3, b1group3())
                .group(4, b1group4());
    }

    private static UnaryOperator<SectionGroupBuilder> b1group1() {
        return g3 -> new MissionSectionBuilder(g3.name("GOD IS ..."))
                .completeWeek("GOD IS CREATOR")
                .completeWeek("GOD IS HOLY")
                .completeWeek("GOD IS JUST")
                .completeWeek("GOD IS LOVE")
                .completeWeek("GOD IS ETERNAL")
                .review("GOD IS ... REVIEW")
                .go("GOD IS WITH YOU").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b1group2() {
        return g3 -> new MissionSectionBuilder(g3.name("THE BIBLE"))
                .completeWeek("THE BIBLE IS TRUE AND WITHOUT ERROR")
                .completeWeek("THE BIBLE HELPS US KNOW GOD")
                .completeWeek("THE BIBLE SHOULD BE STUDIED CAREFULLY")
                .completeWeek("THE BIBLE TEACHES US ABOUT PEOPLE FROM THE PAST")
                .completeWeek("THE BIBLE TEACHES US HOW TO LIVE")
                .completeWeek("THE BIBLE IS POWERFUL AND ACTIVE")
                .review("THE BIBLE REVIEW")
                .go("THE BIBLE IS OUR GUIDE").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b1group3() {
        return g3 -> new MissionSectionBuilder(g3.name("JESUS"))
                .completeWeek("JESUS IS FULLY GOD")
                .completeWeek("JESUS IS FULLY MAN")
                .completeWeek("JESUS IS SAVIOR")
                .completeWeek("JESUS CONQUERED DEATH")
                .completeWeek("JESUS WANTS US TO FOLLOW HIM")
                .completeWeek("JESUS SHOWED GRACE TO OTHERS")
                .review("JESUS REVIEW")
                .go("JESUS WANTS YOU TO SHARE THE GOSPEL").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b1group4() {
        return g3 -> new MissionSectionBuilder(g3.name("LIVING BY GRACE"))
                .completeWeek("WHAT IS GRACE")
                .completeWeek("GRACE AND THE LAW")
                .completeWeek("GRACE TO OBEY")
                .completeWeek("GRACE TO FORGIVE")
                .completeWeek("GRACE AND PAUL")
                .completeWeek("GRACE IN ACTION")
                .review("LIVING BY GRACE REVIEW").parent();
    }

    private static BookBuilder missionB2Structure(BookBuilder builder) {
        return builder
                .group(1, b2group1())
                .group(2, b2group2())
                .group(3, b2group3())
                .group(4, b2group4());
    }

    private static UnaryOperator<SectionGroupBuilder> b2group1() {
        return g3 -> new MissionSectionBuilder(g3.name("GOD IS ..."))
                .completeWeek("GOD IS TRUTH")
                .completeWeek("GOD IS ALL-POWERFUL")
                .completeWeek("GOD IS EVERYWHERE")
                .completeWeek("GOD IS ALL-KNOWING")
                .completeWeek("GOD IS THREE IN ONE")
                .review("GOD IS ... REVIEW").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b2group2() {
        return g3 -> new MissionSectionBuilder(g3.name("THE BIBLE"))
                .completeWeek("BOOKS OF HISTORY")
                .completeWeek("BOOKS OF WISDOM")
                .completeWeek("BOOKS OF PROPHECY")
                .completeWeek("THE GOSPELS")
                .completeWeek("ACTS")
                .completeWeek("THE EPISTLES")
                .completeWeek("REVELATIONS")
                .review("THE BIBLE REVIEW").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b2group3() {
        return g3 -> new MissionSectionBuilder(g3.name("REDEMPTION"))
                .completeWeek("IMAGE OF GOD")
                .completeWeek("SATAN")
                .completeWeek("THE FALL")
                .completeWeek("SIN AND SUFFERING")
                .completeWeek("JESUS CHRIST")
                .completeWeek("THE LORD'S RETURN")
                .completeWeek("ETERNITY")
                .review("REDEMPTION REVIEW").parent();
    }

    private static UnaryOperator<SectionGroupBuilder> b2group4() {
        return g3 -> new MissionSectionBuilder(g3.name("EVIDENCE"))
                .completeWeek("FAITH")
                .completeWeek("PRAYER")
                .completeWeek("STUDYING GOD'S WORD")
                .completeWeek("WORSHIP")
                .completeWeek("FELLOWSHIP")
                .completeWeek("WITNESSING")
                .review("EVIDENCE REVIEW").parent();
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
