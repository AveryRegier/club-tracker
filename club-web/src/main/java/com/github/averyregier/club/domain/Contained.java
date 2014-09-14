package com.github.averyregier.club.domain;

/**
 * Created by rx39789 on 9/12/2014.
 */
public interface Contained<C extends HasId> extends HasId {
    public C getContainer();
}
