package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.adapter.AwardBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;

import static com.github.averyregier.club.domain.program.awana.TnTMissionSectionTypes.*;

/**
 * Created by avery on 8/22/16.
 */
public class MissionSectionBuilder {
    private SectionGroupBuilder b;
    private AwardBuilder silver;
    private AwardBuilder gold;
    private int sequence = 0;

    public MissionSectionBuilder(SectionGroupBuilder b) {
        this.b = b;
        silver = new AwardBuilder().name("Silver " + b.getSequence());
        gold = new AwardBuilder().name("Gold " + b.getSequence());
    }

    public MissionSectionBuilder completeWeek(String name)
    {
        b.section(++sequence, regular).name(name)
         .section(sequence).name("SILVER").award(silver)
         .section(sequence).name("GOLD").award(gold);
        return this;
    }

    public MissionSectionBuilder review(String name) {
        b.section(++sequence, review).name(name);
        return this;
    }

    public MissionSectionBuilder go(String name) {
        b.section(++sequence, go).name(name);
        return this;
    }

    public SectionGroupBuilder parent() {
        return b;
    }
}
