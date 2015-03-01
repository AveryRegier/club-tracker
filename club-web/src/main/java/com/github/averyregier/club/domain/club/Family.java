package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;

import java.util.Set;

/**
 * Created by avery on 9/5/2014.
 */
public interface Family extends Group, HasId {
    public Set<Parent> getParents();
    public Family update(RegistrationInformation information);
    public RegistrationInformation getRegistration();
}
