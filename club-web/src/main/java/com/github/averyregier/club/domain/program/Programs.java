package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;

/**
 * Created by avery on 9/22/14.
 */
public enum Programs {
    AWANA {
        private Curriculum awana = new CurriculumBuilder()
                .shortCode("AWANA")
                .curriculum(c -> TnTCurriculum.build(c))
                .build();

        @Override
        public Curriculum get() {
            return awana;
        }
    };

    public abstract Curriculum get();
}
