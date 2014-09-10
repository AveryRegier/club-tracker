package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.SectionGroup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SectionGroupAdapterTest{
    @Test
    public void test1section() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .addSection(new SectionBuilder(0))
                .build();
        assertEquals(sectionGroup.getSections(), sectionGroup.getSections());
        assertEquals(sectionGroup, sectionGroup.getSections().get(0).getGroup());
    }

    @Test
    public void asBook() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .build();
        assertFalse(sectionGroup.asBook().isPresent());
    }

    @Test
    public void noCompletionAward() {
        SectionGroup sectionGroup = new SectionGroupBuilder(1)
                .build();
        assertFalse(sectionGroup.getCompletionReward().isPresent());
    }
}