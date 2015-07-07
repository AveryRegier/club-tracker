package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.utility.TrackedField;
import com.github.averyregier.club.domain.utility.TrackedString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;
import static java.util.Collections.unmodifiableList;

/**
 * Created by avery on 7/5/15.
 */
public class NameBuilder {

    private TrackedString given, surname, friendly, honorific;
    private TrackedField<Optional<String>> title;
    private TrackedField<List<String>> middle;
    private boolean built = false;
    private boolean override;

    public NameBuilder() {
        forNewName();
    }

    private void forNewName() {
        this.override = true;
        this.given = new TrackedString("");
        this.surname = new TrackedString("");
        this.friendly = new TrackedString("");
        this.honorific = new TrackedString("");
        this.title = new TrackedField<>(Optional.empty());
        this.middle = new TrackedField<>(Collections.emptyList());
    }

    public NameBuilder(Name initialName, boolean override) {
        if(initialName == null) {
            forNewName();
        } else {
            this.override = override;
            this.given = new TrackedString(initialName.getGivenName());
            this.surname = new TrackedString(initialName.getSurname());
            this.friendly = new TrackedString(initialName.getFriendlyName());
            this.honorific = new TrackedString(initialName.getHonorificName());
            this.title = new TrackedField<>(initialName.getTitle());
            this.middle = new TrackedField<>(unmodifiableList(initialName.getMiddleNames()));
        }
    }

    public NameBuilder title(String title) {
        Optional<String> value = Optional.ofNullable(killWhitespace(title));
        if(override) {
            this.title.setValue(value);
        } else {
            this.title.test((o) -> !o.isPresent(), () -> value);
        }
        return this;
    }

    public NameBuilder given(String given) {
        setString(this.given, given);
        return this;
    }

    private void setString(TrackedString trackedString, String value) {
        String v = orEmpty(value);
        if(override) {
            trackedString.setValue(v);
        } else {
            trackedString.ifEmpty(()-> v);
        }
    }

    public NameBuilder surname(String surname) {
        setString(this.surname, surname);
        return this;
    }

    public NameBuilder friendly(String friendly) {
        setString(this.friendly, friendly);
        return this;
    }

    public NameBuilder honorific(String honorific) {
        setString(this.honorific, honorific);
        return this;
    }

    public NameBuilder middle(String middle) {
        if(override || this.middle.isChanged() || this.middle.getValue().isEmpty()) {
            if (!this.middle.isChanged()) {
                this.middle.forceSet(new ArrayList<>(3));
            }
            ifPresent(middle, m -> this.middle.getValue().add(middle));
        }
        return this;
    }

    public Name build() {
        if(built) throw new IllegalStateException("Name builder is already built");
        built = true;
        return new ImmutableName(
                given.getValue(),
                surname.getValue(),
                friendly.getValue(),
                title.getValue(),
                honorific.getValue(),
                middle.getValue());
    }

    public boolean isChanged() {
        return given.isChanged() ||
                surname.isChanged() ||
                friendly.isChanged() ||
                title.isChanged() ||
                honorific.isChanged() ||
                middle.isChanged();
    }
}
