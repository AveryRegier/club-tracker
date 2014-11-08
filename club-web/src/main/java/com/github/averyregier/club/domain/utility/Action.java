package com.github.averyregier.club.domain.utility;

/**
 * Created by avery on 11/1/14.
 */
public enum Action {
    spouse("Add Spouse");

    private String displayName;

    Action(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
