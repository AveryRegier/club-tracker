package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.program.SectionType;

import java.util.Optional;

/**
 * Created by rx39789 on 9/7/2014.
 */
public class SectionBuilder {
    private int sequence;

    public SectionBuilder(int sequence) {
        this.sequence = sequence;
    }

    public Section build() {
        return new Section() {
            @Override
            public SectionType getSectionType() {
                return null;
            }

            @Override
            public SectionGroup getGroup() {
                return null;
            }

            @Override
            public Optional<SectionGroup> getRewardGroup() {
                return null;
            }

            @Override
            public int sequence() {
                return sequence;
            }

            @Override
            public String getId() {
                return null;
            }
        };
    }
}
