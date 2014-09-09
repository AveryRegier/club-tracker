package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;

import java.util.Optional;

/**
* Created by rx39789 on 9/6/2014.
*/
abstract class MockSection implements Section {
    private final int sequence;
    private final SectionType sectionType;

    public MockSection(int sequence, SectionType sectionType) {
        this.sequence = sequence;
        this.sectionType = sectionType;
    }

    @Override
    public SectionType getSectionType() {
        return sectionType;
    }

    @Override
    public Optional<SectionGroup> getRewardGroup() {
        return !getSectionType().requiredForBookReward() ? Optional.of(getGroup()) : Optional.empty();
    }

    @Override
    public int sequence() {
        return sequence;
    }

    @Override
    public String getId() {
        SectionGroup group = getGroup();
        return (group==null? "null" : group.getId())+"."+Integer.toString(sequence);
    }

    @Override
    public String toString() {
        return getId();
    }
}
