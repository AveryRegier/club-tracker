package com.github.averyregier.club.domain.policy;

/**
 * Created by rx39789 on 9/5/2014.
 */
public interface Policy {
    public PolicyComparisonType getComparisonType();
    public String getName();
    public String getRequirement();
    public String getUnit();
}
