package com.github.averyregier.club.domain.club;

import java.util.Set;

/**
 * Created by avery on 9/5/2014.
 */
public interface Family extends Group {
    public Set<Parent> getParents();
    public Family update(RegistrationInformation information);
    public RegistrationInformation getRegistration();
}
