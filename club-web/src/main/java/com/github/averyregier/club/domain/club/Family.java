package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;

import java.util.Set;

/**
 * Created by avery on 9/5/2014.
 */
public interface Family extends Group, HasId {
    Set<Parent> getParents();

    @Override
    default String getShortCode() {
        return getParents().stream()
                .findFirst()
                .map(p->"The "+p.getName().getSurname()+" Family")
                .orElse(getId());
    }

}
