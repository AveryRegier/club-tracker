package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.Builder;
import com.github.averyregier.club.domain.utility.builder.ChildBuilder;

/**
 * Created by avery on 8/22/15.
 */
public interface InputFieldDesignatorBuilder<A extends InputFieldDesignator> extends Builder<A>,
        ChildBuilder<InputFieldGroup, InputFieldDesignator> {
    InputFieldDesignatorBuilder<A> copy(A toCopy);
}
