package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.program.Section;

import java.util.List;
import java.util.function.Predicate;

/**
* Created by avery on 10/10/14.
*/
class AwardSequence extends AwardAdapter {
    private List<Catalogued> sequence;

    public AwardSequence(List<Catalogued> sequence, String name, List<Section> builtSections, AccomplishmentLevel accomplishmentLevel) {
        super(name, builtSections, accomplishmentLevel);
        this.sequence = sequence;
    }

    @Override
    public List<Catalogued> list() {
        return sequence;
    }

    @Override
    public Catalogued select(Predicate<Catalogued> filter) {
        if(filter == null) return sequence.get(0);
        return sequence.stream().filter(filter).findFirst().orElse(sequence.get(0));
    }
}
