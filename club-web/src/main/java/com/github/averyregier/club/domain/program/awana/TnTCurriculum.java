package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;

/**
 * Created by rx39789 on 9/12/2014.
 */
public class TnTCurriculum {

    public static final Curriculum curriculum = new CurriculumBuilder()
            .setShortCode("TnT")
            .addBook(0, b->b
                .setShortCode("UA:SZ")
                .addReward(a->a)
                .addSectionGroup(0, g->
                        g.addReward(r->r
                                .addSection(1, TnTSectionTypes.regular)
                                .addSection(2, TnTSectionTypes.regular)
                                .addSection(3, TnTSectionTypes.regular)
                                .addSection(4, TnTSectionTypes.regular)
                                .addSection(5, TnTSectionTypes.regular)
                                .addSection(6, TnTSectionTypes.regular)
                                .addSection(7, TnTSectionTypes.regular)
                        ))).build();

    public static Curriculum get() {
        return curriculum;
    }
}
