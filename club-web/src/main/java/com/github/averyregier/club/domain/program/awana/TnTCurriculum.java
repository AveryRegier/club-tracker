package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.adapter.RewardBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;

import java.util.function.UnaryOperator;

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
                        .book(1, book1()))
                .curriculum(c -> c
                        .shortCode("UC")
                );
    }

    private static UnaryOperator<BookBuilder> startZone() {
        return b -> b
                .shortCode("SZ")
                .name("Ultimate Adventure Start Zone")
                .ageGroup(AgeGroup.DefaultAgeGroup.THIRD_GRADE)
                .ageGroup(AgeGroup.DefaultAgeGroup.FOURTH_GRADE)
                .publicationYear(2010)
                .catalog("80881")
                .reward(a -> a)
                .group(0, g ->
                        g.reward(r -> r
                                .section(1, TnTSectionTypes.regular)
                                .section(2, TnTSectionTypes.regular)
                                .section(3, TnTSectionTypes.regular)
                                .section(4, TnTSectionTypes.regular)
                                .section(5, TnTSectionTypes.regular)
                                .section(6, TnTSectionTypes.regular)
                                .section(7, TnTSectionTypes.regular)));
    }

    private static UnaryOperator<BookBuilder> book1() {

        return b -> {
            BookBuilder builder = b
                    .shortCode("1")
                    .name("Ultimate Adventure Book 1")
                    .ageGroup(AgeGroup.DefaultAgeGroup.THIRD_GRADE)
                    .ageGroup(AgeGroup.DefaultAgeGroup.FOURTH_GRADE)
                    .publicationYear(2010)
                    .catalog("80434", "Ea.")
                    .catalog("80422", "Pkg.");
            return tntStructure(builder);
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

    private static UnaryOperator<SectionGroupBuilder> discovery(int ordinal, RewardBuilder silver1, RewardBuilder gold1) {
        return g -> g
                .name("Discovery "+ordinal)
                .reward(r -> r
                        .section(0, TnTSectionTypes.parent)
                        .section(1, TnTSectionTypes.regular)
                        .section(2, TnTSectionTypes.regular)
                        .section(3, TnTSectionTypes.regular)
                        .section(4, TnTSectionTypes.regular)
                        .section(5, TnTSectionTypes.regular)
                        .section(6, TnTSectionTypes.regular)
                        .section(7, TnTSectionTypes.regular)
                )
                .reward(silver1
                        .section(8, TnTSectionTypes.extaCredit, s->s.shortCode("S")))
                .reward(gold1
                        .section(9, TnTSectionTypes.extaCredit, s -> s.shortCode("G1"))
                        .section(10, TnTSectionTypes.extaCredit, s -> s.shortCode("G2"))
                );
    }
}
