package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;

public class TnTCombinedCurriculum {

    public static final Curriculum curriculum = build(new CurriculumBuilder()).build();

    public static Curriculum get() {
        return curriculum;
    }

    public static CurriculumBuilder build(CurriculumBuilder builder) {
        CurriculumBuilder tnt = builder.shortCode("TnT");
        return tnt.curriculum(mission->TnTMissionCurriculum.buildMission(mission.shortCode("M").name("Mission"))).
                curriculum(ultimate->TnTCurriculum.buildUltimate(ultimate.shortCode("U").name("Ultimate")));
    }
}
