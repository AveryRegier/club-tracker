package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.utility.DisplayNamed;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by avery on 9/6/2014.
 */
public interface Award extends SectionHolder, DisplayNamed {
    public AccomplishmentLevel getAccomplishmentLevel();
    public Catalogued select(Predicate<Catalogued> filter);
    public List<Catalogued> list();

    public Catalogued select();

    default boolean isCompleted(ClubberRecord clubberRecord) {
        return getSections().stream().allMatch(clubberRecord.isSigned());
    }
}
