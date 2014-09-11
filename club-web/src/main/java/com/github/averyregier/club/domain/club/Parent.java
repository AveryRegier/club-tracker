package com.github.averyregier.club.domain.club;

import java.util.Optional;

/**
 * Created by rx39789 on 9/5/2014.
 */
public interface Parent extends Person {
    public Family register(RegistrationInformation information);
    public Optional<Family> getFamily();
}