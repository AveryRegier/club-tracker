package com.github.averyregier.club.domain;

/**
 * Created by avery on 9/12/2014.
 */
public interface Contained<C extends HasId> extends HasId {
    public C getContainer();
}
