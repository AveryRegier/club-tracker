package com.github.averyregier.club.domain.club;

import java.util.Set;

/**
 * Created by avery on 9/5/2014.
 */
public interface Listener extends ClubWorker {
    Set<Clubber> getQuickList();
}
