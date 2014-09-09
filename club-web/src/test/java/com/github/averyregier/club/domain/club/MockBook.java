package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.adapter.BookAdapter;
import com.github.averyregier.club.domain.program.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
* Created by rx39789 on 9/6/2014.
*/
class MockBook extends BookAdapter implements Book {
    private Reward reward = new Reward() {};

    private List<SectionGroup> groups = new ArrayList<>(5);
    @Override
    public List<SectionGroup> getSectionGroups() {
        return groups;
    }

    @Override
    public List<AgeGroup> getAgeGroups() {
        return Collections.emptyList();
    }

    @Override
    public int sequence() {
        return 0;
    }

    @Override
    public Optional<Reward> getCompletionReward() {
        return Optional.of(reward);
    }

    @Override
    public String getId() {
        return Integer.toString(sequence());
    }

    void addSectionGroup(SectionGroup group) {
        groups.add(group);
    }
}
