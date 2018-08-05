package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.*;

import java.util.function.UnaryOperator;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.*;
import static com.github.averyregier.club.domain.program.awana.TnTSectionTypes.*;

/**
 * Created by avery on 9/12/2014.
 */
public class TnTCurriculum {

    public static final Curriculum curriculum = build(new CurriculumBuilder()).build();

    public static Curriculum get() {
        return curriculum;
    }

    public static CurriculumBuilder build(CurriculumBuilder builder) {
        return buildUltimate(builder.shortCode("TnT"));
    }

    public static CurriculumBuilder buildUltimate(CurriculumBuilder builder) {
        return builder
                .curriculum(c -> c
                        .shortCode("UA")
                        .name("Ultimate Adventure")
                        .book(0, startZone())
                        .book(1, book1())
                        .book(2, book2()))
                .curriculum(c -> c
                        .shortCode("UC")
                        .name("Ultimate Challenge")
                        .book(0, ucStartZone())
                        .book(1, book3())
                        .book(2, book4()));
    }

    private static UnaryOperator<BookBuilder> startZone() {
        return b -> b
                .shortCode("SZ")
                .mwhCode("UASZ")
                .name("Ultimate Adventure Start Zone")
                .ageGroup(THIRD_GRADE)
                .ageGroup(FOURTH_GRADE)
                .publicationYear(2010)
                .catalog("80881")
                .group(0, g ->
                        g.name("Start Zone")
                         .award(r -> r
                                 .name("T&T Ultimate Adventure Uniform")
                                 .section(1, regular)
                                 .section(2, regular)
                                 .section(3, regular)
                                 .section(4, regular)
                                 .section(5, regular)
                                 .section(6, regular)
                                 .section(7, regular)));
    }

    private static UnaryOperator<BookBuilder> book1() {
        return b -> {
            b.shortCode("1")
             .mwhCode("UA1")
             .name("Ultimate Adventure Book 1")
             .ageGroup(THIRD_GRADE)
             .ageGroup(FOURTH_GRADE)
             .publicationYear(2010)
             .catalog("80434", "Ea.")
             .catalog("80422", "Pkg.")
             .award(r -> r.sequence(s->s
                             .item(TnTCurriculum::alpha)))
             .typeAssigner((g, s) -> {
                 if (s == 0) return parent;
                 else if (s > 8) return gold;
                 else if (s > 7) return silver;
                 else if (g == 5 && s == 7) return friend;
                 else return regular;
             });
            return tntStructure(b);
        };
    }

    private static UnaryOperator<BookBuilder> book2() {
        return b -> {
            b.shortCode("2")
             .mwhCode("UA2")
             .name("Ultimate Adventure Book 2")
             .ageGroup(FOURTH_GRADE)
             .publicationYear(2010)
             .catalog("80493", "Ea.")
             .catalog("80506", "Pkg.")
             .award(r -> r.sequence(s->s
                     .item(TnTCurriculum::alpha)
                     .item(TnTCurriculum::excellence)))
             .typeAssigner((g, s) -> {
                 if (s == 0) return parent;
                 else if (s > 8) return gold;
                 else if (s > 7) return silver;
                 else if (g == 6 && s == 5) return friend;
                 else if (g == 4 && s == 3) return group;
                 else return regular;
             });
            return tntStructure(b);
        };
    }

    private static UnaryOperator<BookBuilder> ucStartZone() {
        return b -> b
                .shortCode("SZ")
                .mwhCode("UCSZ")
                .name("Ultimate Challenge Start Zone")
                .ageGroup(FIFTH_GRADE)
                .ageGroup(SIXTH_GRADE)
                .publicationYear(2010)
                .catalog("78369")
                .award(a -> a)
                .group(0, g ->
                        g.name("Start Zone")
                         .award(r -> r
                                .name("T&T Ultimate Challenge Uniform")
                                .section(1, regular)
                                .section(2, regular)
                                .section(3, regular)
                                .section(4, regular)
                                .section(5, regular)
                                .section(6, regular)
                                .section(7, regular)));
    }

    private static UnaryOperator<BookBuilder> book3() {
        return b -> {
            b.shortCode("1")
             .mwhCode("UC1")
             .name("Ultimate Challenge Book 1")
             .ageGroup(FIFTH_GRADE)
             .ageGroup(SIXTH_GRADE)
             .publicationYear(2010)
             .catalog("80557", "Ea.")
             .catalog("80565", "Pkg.")
             .award(r -> r.sequence(s->s
                            .item(TnTCurriculum::alpha)
                            .item(TnTCurriculum::excellence)
                            .item(TnTCurriculum::challenge)))
             .typeAssigner((g, s) -> {
                 if (s == 0) return parent;
                 else if (s > 8) return gold;
                 else if (s > 7) return silver;
                 else if (g == 1 && s == 7) return friend;
                 else return regular;
             });
            return tntStructure(b);
        };
    }

    private static UnaryOperator<BookBuilder> book4() {
        return b -> {
            b.shortCode("2")
             .mwhCode("UC2")
             .name("Ultimate Challenge Book 2")
             .ageGroup(SIXTH_GRADE)
             .publicationYear(2010)
             .catalog("80611", "Ea.")
             .catalog("80629", "Pkg.")
             .award(r -> r.sequence(s->s
                           .item(TnTCurriculum::alpha)
                           .item(TnTCurriculum::excellence)
                           .item(TnTCurriculum::challenge)
                           .item(TnTCurriculum::timothy)))
             .typeAssigner((g, s) -> {
                 if (s == 0) return parent;
                 else if (s > 8) return gold;
                 else if (s > 7) return silver;
                 else if (g == 1 && s == 7) return friend;
                 else return regular;
             });
            return tntStructure(b);
        };
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

    private static CatalogueBuilder challenge(CatalogueBuilder i) {
        return i.name("T&T Challenge Award")
                .catalog("79572", "Award and Pin")
                .catalog("79581", "Replacement Pin");
    }

    private static CatalogueBuilder timothy(CatalogueBuilder i) {
        return i.name("T&T Timothy Award")
                .catalog("79599", "Award and Pin")
                .catalog("79601", "Replacement Pin");
    }

    private static BookBuilder tntStructure(BookBuilder builder) {

        AwardBuilder silver1 = new AwardBuilder().name("Silver 1");
        AwardBuilder silver2 = new AwardBuilder().name("Silver 2");
        AwardBuilder silver3 = new AwardBuilder().name("Silver 3");
        AwardBuilder silver4 = new AwardBuilder().name("Silver 4");
        AwardBuilder gold1 = new AwardBuilder().name("Gold 1");
        AwardBuilder gold2 = new AwardBuilder().name("Gold 2");
        AwardBuilder gold3 = new AwardBuilder().name("Gold 3");
        AwardBuilder gold4 = new AwardBuilder().name("Gold 4");

        return builder
                .group(1, discovery(1, silver1, gold1))
                .group(2, discovery(2, silver1, gold1))
                .group(3, discovery(3, silver2, gold2))
                .group(4, discovery(4, silver2, gold2))
                .group(5, discovery(5, silver3, gold3))
                .group(6, discovery(6, silver3, gold3))
                .group(7, discovery(7, silver4, gold4))
                .group(8, discovery(8, silver4, gold4));
    }

    private static UnaryOperator<SectionGroupBuilder> discovery(
            int ordinal,
            AwardBuilder silver1,
            AwardBuilder gold1) {
        return g -> g
                .name("Discovery "+ordinal)
                .award(r -> r
                        .section(0)
                        .section(1)
                        .section(2)
                        .section(3)
                        .section(4)
                        .section(5)
                        .section(6)
                        .section(7))
                .award(silver1
                        .section(8, s -> s.shortCode("S")))
                .award(gold1
                        .section(9, s -> s.shortCode("G1"))
                        .section(10, s -> s.shortCode("G2")));
    }
}
