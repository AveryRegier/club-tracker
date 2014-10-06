package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.SectionType;

/**
 * Created by avery on 10/5/14.
 */
public interface SectionTypeDecider {
    public SectionType decide(int groupSequence, int sectionSequence);
}
