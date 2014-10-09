package com.github.averyregier.club.domain.program;

/**
 * Created by avery on 9/6/2014.
 */
public interface Award extends SectionHolder, Catalogued {
    public AwardType getAwardType();
}
