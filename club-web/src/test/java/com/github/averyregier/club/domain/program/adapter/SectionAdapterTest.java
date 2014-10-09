package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.AwardType;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SectionAdapterTest {

    @Test
    public void noAwardGroup() {
        Section section = new SectionBuilder(2, null).build();
        assertTrue(section.getAwards(AwardType.group).isEmpty());
    }

    @Test
    public void sectionType() {
        SectionType sectionType = new SectionType() {};
        Section section = new SectionBuilder(2, sectionType).build();
        assertEquals(sectionType, section.getSectionType());
    }
}