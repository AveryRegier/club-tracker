package com.github.averyregier.club.domain.builder;

import com.github.averyregier.club.domain.Contained;
import com.github.averyregier.club.domain.HasId;

/**
 * Created by rx39789 on 10/3/2014.
 */
public interface ChildBuilder<P extends HasId, C extends Contained<P>> {
    public C build(Later<P> parent);
}
