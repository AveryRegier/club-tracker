package com.github.averyregier.club.domain.program;

import java.util.List;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Book extends SectionGroup {

    public List<SectionGroup> getSectionGroups();
    public List<AgeGroup> getAgeGroups();
}
