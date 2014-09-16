package com.github.averyregier.club.domain.policy;

/**
 * Created by avery on 9/5/2014.
 */
public interface Policy {
    public PolicyComparisonType getComparisonType();
    public String getName();
    public String getRequirement();
    public String getUnit();
}
