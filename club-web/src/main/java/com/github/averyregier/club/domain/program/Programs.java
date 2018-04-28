package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.awana.SparksCurriculum;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import com.github.averyregier.club.domain.program.awana.TnTMissionCurriculum;

import java.util.Optional;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.*;

/**
 * Created by avery on 9/22/14.
 */
public enum Programs {
    AWANA {
        private Curriculum awana = new CurriculumBuilder()
                .shortCode("AWANA")
                .curriculum(c -> c.shortCode("Puggles").accepts(TWO, THREE))
                .curriculum(c -> c.shortCode("Cubbies").accepts(FOUR, FIVE))
                .curriculum(SparksCurriculum::build)
                .curriculum(TnTCurriculum::build)
                .build();

        @Override
        public Curriculum get() {
            return awana;
        }
    },
    AWANA_2018 {
        private Curriculum awana = new CurriculumBuilder()
                .shortCode("AWANA2018")
                .curriculum(c -> c.shortCode("Puggles").accepts(TWO, THREE))
                .curriculum(c -> c.shortCode("Cubbies").accepts(FOUR, FIVE))
                .curriculum(SparksCurriculum::build)
                .curriculum(TnTMissionCurriculum::build)
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
