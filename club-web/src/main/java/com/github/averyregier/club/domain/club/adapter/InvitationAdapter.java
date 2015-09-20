package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Invitation;
import com.github.averyregier.club.domain.club.Person;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by avery on 9/12/15.
 */
public class InvitationAdapter implements Invitation {
    private final Optional<Person> person;
    private final Integer auth;
    private final Optional<Person> sentBy;
    private final Instant sentTime;
    private final Optional<Instant> completedTime;

    public InvitationAdapter(Optional<Person> person,
                             Integer auth,
                             Optional<Person> sentBy,
                             Instant sentTime,
                             Optional<Instant> completedTime) {
        this.person = person;
        this.auth = auth;
        this.sentBy = sentBy;
        this.sentTime = sentTime;
        this.completedTime = completedTime;
    }

    @Override
    public Person getPerson() {
        return person.get();
    }

    @Override
    public int getAuth() {
        return auth;
    }

    @Override
    public Person by() {
        return sentBy.get();
    }

    @Override
    public Instant getSent() {
        return sentTime;
    }

    @Override
    public Optional<Instant> getCompleted() {
        return completedTime;
    }
}
