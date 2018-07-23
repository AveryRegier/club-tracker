package com.github.averyregier.club.domain.utility;

import java.time.ZoneId;

public interface HasTimezone extends HasId {
    ZoneId getTimeZone();
}
