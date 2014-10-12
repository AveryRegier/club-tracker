package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Catalogued;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CatalogueBuilderTest {
    @Test
    public void equals() {
        Catalogued entry1 = new CatalogueBuilder().name("foo").build();
        Catalogued entry2 = new CatalogueBuilder().name("foo").build();

        assertTrue(entry1.equals(entry2));
    }

    @Test
    public void hash() {
        Catalogued entry1 = new CatalogueBuilder().name("foo").build();
        Catalogued entry2 = new CatalogueBuilder().name("foo").build();

        assertEquals(entry1.hashCode(), entry2.hashCode());
    }

}