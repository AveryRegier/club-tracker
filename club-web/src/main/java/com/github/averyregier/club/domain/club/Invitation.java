package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.club.adapter.InvitationAdapter;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by avery on 9/12/15.
 */
public interface Invitation {
    default Invitation complete() {
        return new InvitationAdapter(
                Optional.of(getPerson()),
                getAuth(),
                Optional.of(by()),
                getSent(),
                Optional.of(Instant.now())
        );
    }

    Person getPerson();

    int getAuth();

    Person by();

    Instant getSent();

    Optional<Instant> getCompleted();
}
