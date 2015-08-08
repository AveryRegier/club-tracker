package com.github.averyregier.club.repository;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.ClubberRecordBroker;
import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.program.Section;

import java.util.LinkedHashMap;

/**
 * Created by avery on 8/7/15.
 */
public class PersistedClubber extends ClubberAdapter {

    private final ClubFactory factory;

    public PersistedClubber(ClubFactory factory, Person person) {
        super(person);
        this.factory = factory;
    }

    @Override
    protected LinkedHashMap<Section, ClubberRecord> getRecords() {
        LinkedHashMap<Section, ClubberRecord> map = new LinkedHashMap<>();
        new ClubberRecordBroker(factory.getConnector())
                .find(this, factory.getPersonManager())
                .stream()
                .forEach(r->map.put(r.getSection(), r));
        return map;
    }

    @Override
    protected ClubberRecord createUnsignedRecord(Section section) {
        return new PersistingClubberRecord(this, section, factory.getConnector());
    }

}
