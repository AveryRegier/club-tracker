package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Parent;
import com.github.averyregier.club.domain.club.RegistrationInformation;

import java.util.LinkedHashSet;
import java.util.Set;

/**
* Created by avery on 11/30/14.
*/
public class FamilyAdapter implements Family {
    private final LinkedHashSet<Parent> parents;
    private final LinkedHashSet<Clubber> clubbers;

    public FamilyAdapter(LinkedHashSet<Parent> parents, LinkedHashSet<Clubber> clubbers) {
        this.parents = parents;
        this.clubbers = clubbers;
    }

    @Override
    public Set<Parent> getParents() {
        return parents;
    }

    @Override
    public Family update(RegistrationInformation information) {
        return this;
    }

    @Override
    public RegistrationInformation getRegistration() {
        return null;
    }

    @Override
    public Set<Clubber> getClubbers() {
        return clubbers;
    }
}
