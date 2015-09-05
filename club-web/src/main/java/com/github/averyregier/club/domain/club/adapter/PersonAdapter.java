package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.utility.InputField;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
* Created by avery on 12/28/14.
*/
public class PersonAdapter implements Person, PersonUpdater {
    private String uuid;
    private Name name;
    private Gender gender;
    private String email;
    private Listener listener;
    private ClubLeader leader;
    private Parent parent;
    private Clubber clubber;
    private Family family;
    private AgeGroup currentAgeGroup;
    private User login;
    private LinkedHashMap<InputField, String> registration = new LinkedHashMap<>();

    public PersonAdapter() {
        this.uuid = null;
    }

    public PersonAdapter(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getId() {
        return uuid;
    }

    public Name getName() {
        return name;
    }

    @Override
    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }

    @Override
    public Optional<User> getLogin() {
        return Optional.ofNullable(login);
    }

    @Override
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    @Override
    public Optional<Parent> asParent() {
        return Optional.ofNullable(parent);
    }

    protected boolean knowsPlaceInFamily() {
        return parent != null || clubber != null;
    }

    @Override
    public Optional<Listener> asListener() {
        return Optional.ofNullable(listener);
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.ofNullable(leader);
    }

    @Override
    public Optional<Family> getFamily() {
        return Optional.ofNullable(family);
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.ofNullable(clubber);
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void setClubber(Clubber clubber) {
        this.clubber = clubber;
    }

    @Override
    public void setFamily(Family family) {
        this.family = family;
    }

    public void setLeader(ClubLeader leader){
        this.leader = leader;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public PersonUpdater getUpdater() {
        return this;
    }

    public AgeGroup getCurrentAgeGroup() {
        return currentAgeGroup;
    }

    @Override
    public void setAgeGroup(AgeGroup ageGroup) {
        this.currentAgeGroup = ageGroup;
    }

    @Override
    public Optional<Clubber> asClubberNow() {
        return Optional.ofNullable(clubber);
    }

    @Override
    public Person asPerson() {
        return this;
    }

    @Override
    public boolean knowsFamily() {
        return family != null;
    }

    @Override
    public boolean equals(Object obj) {
        return uuid != null && obj instanceof PersonAdapter ?  uuid.equals(((PersonAdapter)obj).uuid) : super.equals(obj);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : super.hashCode();
    }

    @Override
    public void setLogin(User login) {
        this.login = login;
    }

    @Override
    public void setAddress(Address address) {
        Family theFamily = getFamily().orElseGet(() -> {
            FamilyAdapter family = new FamilyAdapter(PersonAdapter.this);
            setFamily(family);
            return family;
        });
        if(!theFamily.getAddress().isPresent()) {
            theFamily.setAddress(address);
        } else {
            theFamily.setAddress(new AddressAdapter(theFamily.getAddress().get().getId(),
                    address.getLine1(),
                    address.getLine2(),
                    address.getCity(),
                    address.getPostalCode(),
                    address.getTerritory(),
                    address.getCountry()));
        }
    }

    @Override
    public void setValue(InputField field, String value) {
        registration.put(field, value);
    }

    @Override
    public String getValue(InputField field) {
        return registration.get(field);
    }

    @Override
    public Map<InputField, String> getValues() {
        return Collections.unmodifiableMap(registration);
    }

    public void setValues(Map<InputField, String> values) {
        this.registration = new LinkedHashMap<>(values);
    }
}
