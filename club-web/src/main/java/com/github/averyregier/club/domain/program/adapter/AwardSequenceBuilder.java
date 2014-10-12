package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.utility.builder.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Created by avery on 10/10/14.
 */
public class AwardSequenceBuilder implements Builder<List<Catalogued>> {
    private List<Catalogued> list = new ArrayList<>();

    public AwardSequenceBuilder item(UnaryOperator<CatalogueBuilder> fn) {
        list.add(fn.apply(new CatalogueBuilder()).build());
        return this;
    }


    @Override
    public List<Catalogued> build() {
        return list;
    }
}
