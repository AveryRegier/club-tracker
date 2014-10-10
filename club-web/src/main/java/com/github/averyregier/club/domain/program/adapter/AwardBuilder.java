package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/11/2014.
 */
public class AwardBuilder extends SectionHolderBuilder<AwardBuilder> implements Builder<Award> {
    private Later<Award> futureAward = new Later<>();
    private List<Section> builtSections = new ArrayList<>();
    private AccomplishmentLevel accomplishmentLevel;

    @SuppressWarnings("unchecked")
    List<Section> build(Later<SectionGroup> futureGroup, List<Later<Award>> bookRewards) {
        applyDecider();
        List<Section> currentSections = buildSections(futureGroup,
                UtilityMethods.concat(futureAward, bookRewards));
        builtSections.addAll(currentSections);
        return currentSections;
    }


    public Award build() {
        if(!sections.isEmpty()) {
            throw new IllegalStateException();
        }
        Award award = new AwardAdapter(name, builtSections, accomplishmentLevel);
        futureAward.set(award);
        return award;
    }

    @SuppressWarnings("unchecked")
    private synchronized List<Section> buildSections(Later<SectionGroup> futureGroup, List<Later<Award>> futureReward) {
        List<SectionBuilder> relevant = sections.stream()
                .filter(s -> s.getGroup() == futureGroup)
                .collect(Collectors.toList());
        List<Section> collect = relevant.stream()
                .map(b -> b.setGroup(futureGroup)
                           .addAwards(futureReward)
                           .build())
                .collect(Collectors.toList());
        sections.removeAll(relevant);
        return collect;
    }

    public void section(Section s) {
        builtSections.add(s);
    }

    public void type(AccomplishmentLevel accomplishmentLevel) {
        this.accomplishmentLevel = accomplishmentLevel;
    }

    void identifySectionGroup(Later<SectionGroup> futureGroup) {
        sections.forEach(s->s.identifyFutureGroup(futureGroup));
    }
}
