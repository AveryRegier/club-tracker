package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;

import java.util.Optional;

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

    public static Optional<Curriculum> find(String curriculum) {
        for(Programs program: values()) {
            if(program.get().getShortCode().equals(curriculum)) {
                return Optional.of(program.get());
            }
            Optional<Curriculum> series = program.get().getSeries(curriculum);
            if(series.isPresent()) {
                return series;
            }
        }
        return Optional.empty();
    }
}
