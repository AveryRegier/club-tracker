package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.utility.Contained;

import java.util.List;

/**
 * Created by avery on 9/6/2014.
 */
public interface Book extends SectionHolder, Catalogued, Contained<Curriculum> {

    public List<SectionGroup> getSectionGroups();
    public int sequence();
    public List<AgeGroup> getAgeGroups();

    BookVersion getVersion();

}
