package com.github.averyregier.club.domain.program;

import com.github.averyregier.club.domain.club.AwardPresentation;
import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.utility.DisplayNamed;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by avery on 9/6/2014.
 */
public interface Award extends SectionHolder, DisplayNamed {
    public AccomplishmentLevel getAccomplishmentLevel();
    public Catalogued award(Predicate<Catalogued> filter);
    public List<Catalogued> list();

    public Catalogued award();

    default boolean isCompleted(ClubberRecord clubberRecord) {
        return getSections().stream().allMatch(clubberRecord.isSigned());
    }

    default Predicate<Catalogued> mayAwardMatcher(Stream<AwardPresentation> stream) {
        List<AwardPresentation> collected = stream.collect(Collectors.toList());
        return c -> collected.stream()
                .filter(a -> a.token().isPresent())
                .allMatch(a -> !a.token().get().equals(c));
    }

    default Catalogued selectAwarded(Supplier<Stream<AwardPresentation>> alreadyAwarded) {
        return award(mayAwardMatcher(alreadyAwarded.get()));
    }
}
