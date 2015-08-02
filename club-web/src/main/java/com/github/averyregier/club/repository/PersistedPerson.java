package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.ClubberBroker;
import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.broker.FamilyBroker;
import com.github.averyregier.club.broker.ParentBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Parent;
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
    private final Connector connector;
    private final String id;
    private PersonManager manager;
    private Supplier<Optional<String>> parentFamilyLookup = lookupFamilyFn();
    private Supplier<Optional<Parent>> parentLookup = lookupParentFn();
    private Supplier<Optional<Clubber>> clubberLookup =lookupClubberFn();

    public PersistedPerson(Connector connector, String id, PersonManager manager) {
        super(id);
        this.connector = connector;
        this.id = id;
        this.manager = manager;
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

    private Supplier<Optional<Parent>> lookupParentFn() {
        return setOnce(() -> parentFamilyLookup.get()
                        .map((f) -> new ParentAdapter(this)),
                this::setParent);
    }

    private Supplier<Optional<Clubber>> lookupClubberFn() {
        return setOnce(() -> new ClubberBroker(connector).find(getId(), manager, null),
                this::setClubber);
    }

    private Supplier<Optional<String>> lookupFamilyFn() {
        return setOnce(() -> new ParentBroker(connector).findFamily(getId()),
                (id) -> setFamily(loadFamily(id)));
    }

    private PersistedFamily loadFamily(String id) {
        return new PersistedFamily(id,
                new FamilyBroker(connector)
                        .getAllFamilyMembers(id)
                        .map(i -> manager.lookup(i).get())
                        .collect(Collectors.toList()));
    }

}
