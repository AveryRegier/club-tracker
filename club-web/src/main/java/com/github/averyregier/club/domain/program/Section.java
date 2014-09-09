package com.github.averyregier.club.domain.program;

import java.util.Optional;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Section {
    public SectionType getSectionType();
    public SectionGroup getGroup();
    public Optional<SectionGroup> getRewardGroup();

    int sequence();

    String getId();
}
