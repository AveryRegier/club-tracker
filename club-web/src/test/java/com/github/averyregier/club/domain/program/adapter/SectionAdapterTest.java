package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionType;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SectionAdapterTest {

    @Test
    public void noRewardGroup() {
        Section section = new SectionBuilder(2, null).build();
        assertNotNull(section.getRewardGroup());
        assertFalse(section.getRewardGroup().isPresent());
    }

    @Test
    public void sectionType() {
        SectionType sectionType = new SectionType() {};
        Section section = new SectionBuilder(2, sectionType).build();
        assertEquals(sectionType, section.getSectionType());
    }
}