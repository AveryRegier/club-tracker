package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Section;

import java.util.List;
import java.util.Optional;

/**
 * Created by rx39789 on 9/5/2014.
 */
public interface Clubber extends ClubMember {
    public Optional<Section> getNextSection();

    List<ClubberRecord> getNextSections(int max);

    Optional<ClubberRecord> getRecord(Optional<Section> section);
}
