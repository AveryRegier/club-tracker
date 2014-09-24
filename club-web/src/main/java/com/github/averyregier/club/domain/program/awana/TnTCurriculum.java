package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;

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
                .book(0, b -> b
                        .shortCode("UA:SZ")
                        .reward(a -> a)
                        .group(0, g ->
                                g.reward(r -> r
                                                .section(1, TnTSectionTypes.regular)
                                                .section(2, TnTSectionTypes.regular)
                                                .section(3, TnTSectionTypes.regular)
                                                .section(4, TnTSectionTypes.regular)
                                                .section(5, TnTSectionTypes.regular)
                                                .section(6, TnTSectionTypes.regular)
                                                .section(7, TnTSectionTypes.regular)
                                )));
    }
}
