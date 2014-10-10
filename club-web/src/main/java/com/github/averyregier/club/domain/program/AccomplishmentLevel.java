package com.github.averyregier.club.domain.program;

/**
 * Created by avery on 9/10/2014.
 */
public enum AccomplishmentLevel {
    book{
        @Override
        public boolean isBook() {
            return true;
        }
    }, group;

    public boolean isBook() {
        return false;
    }
}
