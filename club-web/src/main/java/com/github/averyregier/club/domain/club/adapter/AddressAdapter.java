package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Address;
import com.github.averyregier.club.domain.utility.adapter.CountryValue;

import java.util.UUID;

/**
 * Created by avery on 8/16/15.
 */
public class AddressAdapter implements Address {
    private final String id;

    private String line1, line2, city, postalCode, territory;
    private CountryValue country;

    public AddressAdapter(String line1, String line2, String city, String postalCode, String territory, CountryValue country) {
        this(UUID.randomUUID().toString(), line1, line2, city, postalCode, territory, country);
    }

    public AddressAdapter(String id, String line1, String line2, String city, String postalCode, String territory, CountryValue country) {
        this.id = id;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.postalCode = postalCode;
        this.territory = territory;
        this.country = country;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getShortCode() {
        return id;
    }

    @Override
    public String getLine1() {
        return line1;
    }

    @Override
    public String getLine2() {
        return line2;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String getTerritory() {
        return territory;
    }

    @Override
    public CountryValue getCountry() {
        return country;
    }

    public boolean isEmpty() {
        return line1 == null && line2 == null && city == null && postalCode == null && territory == null;
    }
}
