package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.AwardBroker;
import com.github.averyregier.club.broker.CeremonyBroker;
import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.Named;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by avery on 8/9/15.
 */
public class PersistedAwardPresentation implements AwardPresentation {

    private final String presentationId;
    private final String id;
    private final String accomplishment;
    private final Clubber clubber;
    private final Section section;
    private final String token;
    private Connector connector;
    private Ceremony ceremony;

    public PersistedAwardPresentation(Connector connector, String presentationId, String id, String accomplishment, Clubber clubber, Section section, String token) {
        this.presentationId = presentationId;
        this.id = id;
        this.accomplishment = accomplishment;
        this.clubber = clubber;
        this.section = section;
        this.token = token;
        this.connector = connector;
    }

    @Override
    public boolean notPresented() {
        return presentationId == null && ceremony == null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getShortCode() {
        return findAward().map(Named::getName).orElse(accomplishment);
    }

    @Override
    public Person to() {
        return clubber;
    }

    @Override
    public Named forAccomplishment() {
        return findAward()
                .map(a -> (Named) a)
                .orElse(() -> accomplishment);
    }

    private Optional<Award> findAward() {
        return section.getAwards().stream()
                .filter(a -> a.getName().equals(accomplishment))
                .findFirst();
    }

    @Override
    public LocalDate earnedOn() {
        return Optional.ofNullable(record())
                .map(r -> r.getSigning()
                        .map(Signing::getDate))
                .orElse(Optional.empty())
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public Ceremony presentedAt() {
        if (notPresented()) {
            return null;
        }
        if(presentationId != null && ceremony == null) {
            ceremony = new CeremonyBroker(connector).find(presentationId).orElse(null);
        }
        return ceremony;
    }

    @Override
    public Optional<Catalogued> token() {
        if (token == null) return Optional.empty();
        return Optional.of(findAward()
                .map(a -> a.select(t -> t.getName().equalsIgnoreCase(token)))
                .orElse(() -> token));
    }

    @Override
    public ClubberRecord record() {
        return clubber.getRecord(Optional.ofNullable(section)).orElse(null);
    }

    @Override
    public void presentAt(Ceremony ceremony) {
        this.ceremony = ceremony;
        new AwardBroker(connector).persist(this);
    }
}