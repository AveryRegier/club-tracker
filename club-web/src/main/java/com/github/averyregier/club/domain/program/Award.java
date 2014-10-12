package com.github.averyregier.club.domain.program;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by avery on 9/6/2014.
 */
public interface Award extends SectionHolder {
    public AccomplishmentLevel getAccomplishmentLevel();
    public Catalogued select(Predicate<Catalogued> filter);
    public List<Catalogued> list();

    public Catalogued select();
}
