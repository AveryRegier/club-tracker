package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.adapter.CountryValue;

/**
 * Created by avery on 8/16/15.
 */
public interface Address extends HasId {
    String getLine1();

    String getLine2();

    String getCity();

    String getPostalCode();

    String getTerritory();

    CountryValue getCountry();
}
