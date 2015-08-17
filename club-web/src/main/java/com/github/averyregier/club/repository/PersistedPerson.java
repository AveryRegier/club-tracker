package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.ClubberBroker;
import com.github.averyregier.club.broker.LeaderBroker;
import com.github.averyregier.club.broker.ListenerBroker;
import com.github.averyregier.club.broker.ParentBroker;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.ParentAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;

import java.util.Optional;
import java.util.function.Supplier;

import static com.github.averyregier.club.domain.utility.UtilityMethods.orElseMaybe;
import static com.github.averyregier.club.domain.utility.UtilityMethods.setOnce;

/**
 * Created by avery on 7/11/15.
 */
public class PersistedPerson extends PersonAdapter {
    private final String id;
    private Supplier<Optional<String>> parentFamilyLookup = lookupFamilyFn();
    private Supplier<Optional<Parent>> parentLookup = lookupParentFn();
    private Supplier<Optional<Clubber>> clubberLookup =lookupClubberFn();
    private Supplier<Optional<Listener>> listenerLookup = lookupListenerFn();
    private Supplier<Optional<ClubLeader>> leaderLookup = lookupLeaderFn();
    private ClubFactory factory;

    public PersistedPerson(ClubFactory factory, String id) {
        super(id);
        this.factory = factory;
        this.id = id;
    }

    @Override
    public Optional<Family> getFamily() {
        Optional<Family> family = super.getFamily();
        if(!family.isPresent()) {
            if (!asParent().isPresent()) {
                asClubber();
            }
            return super.getFamily();
        } else return family;
    }

    @Override
    public Optional<Parent> asParent() {
        if(!knowsPlaceInFamily()) return parentLookup.get();
        return super.asParent();
    }

    @Override
    public Optional<Clubber> asClubber() {
        return orElseMaybe(super.asClubber(), clubberLookup);
    }

    @Override
    public Optional<Listener> asListener() {
        return orElseMaybe(super.asListener(), listenerLookup);
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return orElseMaybe(super.asClubLeader(), leaderLookup);
    }

    private Supplier<Optional<Parent>> lookupParentFn() {
        return setOnce(() -> parentFamilyLookup.get()
                        .map((f) -> new ParentAdapter(this)),
                this::setParent);
    }

    private Supplier<Optional<Clubber>> lookupClubberFn() {
        return setOnce(() -> new ClubberBroker(factory).find(getId()),
                this::setClubber);
    }

    private Supplier<Optional<String>> lookupFamilyFn() {
        return setOnce(() -> new ParentBroker(factory.getConnector()).findFamily(getId()),
                (id) -> loadFamily(id).ifPresent(f->setFamily(f)));
    }

    private Optional<Family> loadFamily(String id) {
        return factory.getPersonManager().lookupFamily(id);
    }

    private Supplier<Optional<Listener>> lookupListenerFn() {
        return setOnce(() -> new ListenerBroker(factory.getConnector())
                        .find(getId(), factory.getPersonManager(), factory.getClubManager()),
                this::setListener);
    }

    private Supplier<Optional<ClubLeader>> lookupLeaderFn() {
        return setOnce(() -> new LeaderBroker(factory.getConnector())
                        .find(getId(), factory.getPersonManager(), factory.getClubManager()),
                this::setLeader);
    }
}
