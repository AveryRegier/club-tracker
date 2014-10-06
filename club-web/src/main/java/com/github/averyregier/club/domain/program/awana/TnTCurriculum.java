package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.adapter.RewardBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;

import java.util.function.UnaryOperator;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.FOURTH_GRADE;
import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.THIRD_GRADE;
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
        return builder
                .shortCode("TnT")
                .curriculum(c -> c
                        .shortCode("UA")
                        .book(0, startZone())
                        .book(1, book1())
                        .book(2, book2()))
                .curriculum(c -> c
                                .shortCode("UC")
                );
    }

    private static UnaryOperator<BookBuilder> startZone() {
        return b -> b
                .shortCode("SZ")
                .name("Ultimate Adventure Start Zone")
                .ageGroup(THIRD_GRADE)
                .ageGroup(FOURTH_GRADE)
                .publicationYear(2010)
                .catalog("80881")
                .reward(a -> a)
                .group(0, g ->
                        g.reward(r -> r
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
            b.shortCode("1");
            b.name("Ultimate Adventure Book 1");
            b.ageGroup(THIRD_GRADE);
            b.ageGroup(FOURTH_GRADE);
            b.publicationYear(2010);
            b.catalog("80434", "Ea.");
            b.catalog("80422", "Pkg.");
            b.typeAssigner((g, s) -> {
                if (s == 0) return parent;
                else if (s > 7) return extaCredit;
                else if (g == 5 && s == 7) return friend;
                else return regular;
            });
            return tntStructure(b);
        };
    }

    private static UnaryOperator<BookBuilder> book2() {
        return b -> {
            b.shortCode("2");
            b.name("Ultimate Adventure Book 2");
            b.ageGroup(FOURTH_GRADE);
            b.publicationYear(2010);
            b.catalog("80493", "Ea.");
            b.catalog("80506", "Pkg.");
            b.typeAssigner((g, s) -> {
                if (s == 0) return parent;
                else if (s > 7) return extaCredit;
                else if (g == 6 && s == 5) return friend;
                else if (g == 4 && s == 3) return group;
                else return regular;
            });
            return tntStructure(b);
        };
    }

    private static BookBuilder tntStructure(BookBuilder builder) {

        RewardBuilder silver1 = new RewardBuilder().name("Silver 1");
        RewardBuilder silver2 = new RewardBuilder().name("Silver 2");
        RewardBuilder silver3 = new RewardBuilder().name("Silver 3");
        RewardBuilder silver4 = new RewardBuilder().name("Silver 4");
        RewardBuilder gold1 = new RewardBuilder().name("Gold 1");
        RewardBuilder gold2 = new RewardBuilder().name("Gold 2");
        RewardBuilder gold3 = new RewardBuilder().name("Gold 3");
        RewardBuilder gold4 = new RewardBuilder().name("Gold 4");

        return builder
                .reward()
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
            RewardBuilder silver1,
            RewardBuilder gold1) {
        return g -> g
                .name("Discovery "+ordinal)
                .reward(r -> r
                        .section(0)
                        .section(1)
                        .section(2)
                        .section(3)
                        .section(4)
                        .section(5)
                        .section(6)
                        .section(7)
                )
                .reward(silver1
                        .section(8, s -> s.shortCode("S")))
                .reward(gold1
                        .section(9, s -> s.shortCode("G1"))
                        .section(10, s -> s.shortCode("G2"))
                );
    }
}
