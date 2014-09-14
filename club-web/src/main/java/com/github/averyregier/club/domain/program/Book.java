package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.Contained;

import java.util.List;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Book extends SectionHolder, Catalogued, Contained<Curriculum> {

    public List<SectionGroup> getSectionGroups();
    public int sequence();
    public List<AgeGroup> getAgeGroups();

    BookVersion getVersion();

}
