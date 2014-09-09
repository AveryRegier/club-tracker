package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Reward;

import java.time.LocalDate;
import java.util.Set;

/**
 * Created by rx39789 on 9/6/2014.
 */
public interface Signing {
    public LocalDate getDate();
    public Listener by();
    public String getNote();

    Set<Reward> getCompletionRewards();
}
