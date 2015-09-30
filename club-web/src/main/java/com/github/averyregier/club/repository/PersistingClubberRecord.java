package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.AwardBroker;
import com.github.averyregier.club.broker.ClubberRecordBroker;
import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Section;

import java.time.LocalDate;

/**
 * Created by avery on 8/8/15.
 */
public class PersistingClubberRecord extends ClubberRecord {
    private Clubber clubber;
    private final Section section;
    private Connector connector;

    public PersistingClubberRecord(Clubber clubber, Section section, Connector connector) {
        this.clubber = clubber;
        this.section = section;
        this.connector = connector;
    }

    @Override
    public Section getSection() {
        return section;
    }

    @Override
    public Clubber getClubber() {
        return clubber;
    }

    @Override
    public Signing sign(Listener byListener, String note) {
        return persist(super.sign(byListener, note));
    }

    @Override
    public Signing catchup(Listener byListener, String note, LocalDate date) {
        return persist(super.catchup(byListener, note, date));
    }

    private Signing persist(Signing sign) {
        new ClubberRecordBroker(connector).persist(this);
        AwardBroker broker = new AwardBroker(connector);
        sign.getCompletionAwards().forEach(broker::persist);
        return sign;
    }

    @Override
    protected void persistAward(AwardPresentation awardPresentation) {
        new AwardBroker(connector).persist(awardPresentation);
    }
}
