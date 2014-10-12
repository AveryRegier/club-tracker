package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.utility.builder.Builder;

/**
 * Created by avery on 10/10/14.
 */
public class CatalogueBuilder implements Builder<Catalogued> {
    private String name;

    public CatalogueBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Catalogued build() {
        return new CataloguedAward(name);
    }

    public CatalogueBuilder catalog(String catalogNumber, String quantity) {
        return this;
    }

    private static class CataloguedAward implements Catalogued {
        private String name;

        public CataloguedAward(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof CataloguedAward && ((CataloguedAward)obj).name.equals(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
