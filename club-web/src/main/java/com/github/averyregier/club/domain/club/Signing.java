package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Award;

import java.time.LocalDate;
import java.util.Set;

/**
 * Created by avery on 9/6/2014.
 */
public interface Signing {
    public LocalDate getDate();
    public Listener by();
    public String getNote();

    Set<Award> getCompletionAwards();
}
