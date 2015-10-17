package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasId;

import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/5/2014.
 */
public interface Family extends Group, HasId, Registered {
    Set<Parent> getParents();

    @Override
    default String getShortCode() {
        return getParents().stream()
                .findFirst()
                .map(p->"The "+p.getName().getSurname()+" Family")
                .orElse(getId());
    }

    void addPerson(Person person);

    Optional<Address> getAddress();

    void setAddress(Address address);

    Optional<Clubber> findNthChild(int childNumber);

    boolean shouldInvite();
}
