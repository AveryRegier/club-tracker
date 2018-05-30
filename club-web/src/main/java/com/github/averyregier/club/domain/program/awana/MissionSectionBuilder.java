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
    private int week = 0;

    public MissionSectionBuilder(SectionGroupBuilder b, AwardBuilder discovery) {
        this.b = b;
        this.discovery = discovery;
        silver = new AwardBuilder().name("Silver");
        gold = new AwardBuilder().name("Gold");
    }

    public MissionSectionBuilder completeWeek(String name) {

        discovery.section(++sequence, regular,
                s -> s.name(name).shortCode(getCode()));
        silver.section(++sequence, TnTMissionSectionTypes.silver,
                s -> s.shortCode(week+"S"));
        gold.section(++sequence, TnTMissionSectionTypes.gold,
                s -> s.shortCode(week+"G"));
        return this;
    }

    private String getCode() {
        return Integer.toString(++week);
    }

    public MissionSectionBuilder review(String name) {
        b.award(discovery.section(++sequence, review, s -> s.name(name).shortCode(getCode())));
        return this;
    }

    public MissionSectionBuilder go(String name) {
        b.award(discovery.section(++sequence, go, s -> s.name(name).shortCode(getCode())));
        return this;
    }

    public SectionGroupBuilder parent() {
        return b.award(discovery).award(silver).award(gold);
    }
}
