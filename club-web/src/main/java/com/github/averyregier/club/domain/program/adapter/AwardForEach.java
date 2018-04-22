package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.program.Section;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by avery on 9/5/16.
 */
public class AwardForEach extends AwardAdapter {
    private final int numSections;

    public AwardForEach(String name, List<Section> builtSections, AccomplishmentLevel accomplishmentLevel, int numSections) {
        super(name, builtSections, accomplishmentLevel);
        this.numSections = numSections;
    }

    @Override
    public boolean isCompleted(ClubberRecord clubberRecord) {
        return getSections().stream()
                .map(s->clubberRecord.getClubber().getRecord(Optional.of(s)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ClubberRecord::getSigning)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .count() % numSections == 0;
    }

    @Override
    public List<Catalogued> list() {
        return IntStream.range(1, (getSections().size()/numSections)+1)
                .mapToObj(i-> (Catalogued) () -> getName()+" "+i)
                .collect(Collectors.toList());
    }

    @Override
    public List<Section> getSections() {
        return super.getSections();
    }

    @Override
    public Catalogued award(Predicate<Catalogued> filter) {
        List<Catalogued> list = list();
        if(filter == null) return list.get(0);
        return list.stream().filter(filter).findFirst().orElse(list.get(0));
    }
}
