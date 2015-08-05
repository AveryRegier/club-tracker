package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.ParentAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        if(!asParent().isPresent()) {
            asClubber();
        }
        return super.getFamily();
    }

    @Override
    public Optional<Parent> asParent() {
        if(!knowsFamily()) return parentLookup.get();
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
                (id) -> setFamily(loadFamily(id)));
    }

    private PersistedFamily loadFamily(String id) {
        return new PersistedFamily(id,
                new FamilyBroker(factory.getConnector())
                        .getAllFamilyMembers(id)
                        .map(i -> factory.getPersonManager().lookup(i).get())
                        .collect(Collectors.toList()));
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
