package com.github.averyregier.club.domain.utility;

/**
 * Created by avery on 9/12/2014.
 */
public interface Contained<C extends HasId> extends HasId {
    C getContainer();
}
