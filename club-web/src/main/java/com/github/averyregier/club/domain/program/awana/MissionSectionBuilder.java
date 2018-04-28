package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.adapter.AwardBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;

import static com.github.averyregier.club.domain.program.awana.TnTMissionSectionTypes.*;

/**
 * Created by avery on 8/22/16.
 */
public class MissionSectionBuilder {
    private SectionGroupBuilder b;
    private AwardBuilder discovery;
    private AwardBuilder silver;
    private AwardBuilder gold;
    private int sequence = 0;

    public MissionSectionBuilder(SectionGroupBuilder b) {
        this.b = b;

        discovery = new AwardBuilder().forEach(4).name("Discovery");
        silver = new AwardBuilder().forEach(4).name("Silver");
        gold = new AwardBuilder().forEach(4).name("Gold");
    }

    public MissionSectionBuilder completeWeek(String name)
    {
        b.award(discovery.section(++sequence, regular, s->s.name(name)))
         .award(silver.section(sequence, extraCredit, s->s.shortCode("S")))
         .award(gold.section(sequence, extraCredit, s->s.shortCode("G")));
        return this;
    }

    public MissionSectionBuilder review(String name) {
        b.award(discovery.section(++sequence, review, s->s.name(name)));
        return this;
    }

    public MissionSectionBuilder go(String name) {
        b.award(discovery.section(++sequence, go, s->s.name(name)));
        return this;
    }

    public SectionGroupBuilder parent() {
        return b;
    }
}
