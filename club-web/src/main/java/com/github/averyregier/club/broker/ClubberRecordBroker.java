package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.RecordRecord;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.repository.PersistingClubberRecord;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Record.RECORD;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 3/1/15.
 */
public class ClubberRecordBroker extends PersistenceBroker<ClubberRecord> {
    public ClubberRecordBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(ClubberRecord record, DSLContext create) {
        if(!equalsAny(create.insertInto(RECORD)
                .set(RECORD.CLUBBER_ID, record.getClubber().getId().getBytes())
                .set(RECORD.SECTION_ID, record.getSection().getId())
                .set(mapFields(record))
                .onDuplicateKeyUpdate()
                .set(mapFields(record))
                .execute(), 1, 2)) {
            fail("Clubber persistence failed: " + record.getClubber().getId() + " for " + record.getSection().getId());
        }
    }

    private Map<TableField<RecordRecord, ?>, Object> mapFields(ClubberRecord record) {
        JooqUtil.MapBuilder<RecordRecord> map = JooqUtil.<RecordRecord>map();
        if(record.getSigning().isPresent()) {
            Signing signing = record.getSigning().get();
            map .setHasId(RECORD.CLUB_ID, signing.by().getClub())
                .set(RECORD.SIGNED_BY, signing.by().getId())
                .set(RECORD.SIGN_DATE, signing.getDate())
                .set(RECORD.NOTE, signing.getNote());
        } else {
            map.setNull(RECORD.CLUB_ID, RECORD.SIGNED_BY, RECORD.SIGN_DATE, RECORD.NOTE);
        }
        return map.build();
    }

    public Collection<ClubberRecord> find(Clubber clubber, PersonManager manager) {
        if(!clubber.getClub().isPresent()) return Collections.emptyList();
        return query(create -> create
                .selectFrom(RECORD)
                .where(RECORD.CLUBBER_ID.eq(clubber.getId().getBytes()))
                .and(RECORD.CLUB_ID.eq(clubber.getClub().get().getId().getBytes()))
                .fetch().stream()
                .map(r -> mapClubberRecord(clubber, manager, r))
                .collect(Collectors.toList()));
    }

    private ClubberRecord mapClubberRecord(Clubber clubber, PersonManager manager, RecordRecord r) {
        String sectionId = r.getSectionId();
        String listenerId = convert(r.getSignedBy());
        final Section section = findSection(sectionId, clubber);
        ClubberRecord clubberRecord;
        if (listenerId == null) {
            clubberRecord = new PersistingClubberRecord(clubber, section, connector);
        } else {
            Listener byListener = findListener(listenerId, manager);
            LocalDate localDate = r.getSignDate().toLocalDate();
            String note = r.getNote();
            Signing signing = new Signing() {
                @Override
                public LocalDate getDate() {
                    return localDate;
                }

                @Override
                public Listener by() {
                    return byListener;
                }

                @Override
                public String getNote() {
                    return note;
                }

                @Override
                public Set<AwardPresentation> getCompletionAwards() {
                    return new LinkedHashSet<>(new AwardBroker(connector).find(clubber, section));
                }
            };
            clubberRecord = new PersistingClubberRecord(clubber, section, connector, signing);
        }
        return clubberRecord;
    }

    private Section findSection(String sectionId, Clubber clubber) {
        return clubber.getClub().get().getProgram().getCurriculum().lookup(sectionId)
        .orElseThrow(illegal("section " + sectionId + " does not exist"));
    }

    private Listener findListener(String listenerId, PersonManager manager) {
        Optional<Listener> listener = UtilityMethods.optMap(manager.lookup(listenerId), Person::asListener);
        return listener.orElseThrow(illegal("Listener " + listenerId + " is not a listener"));
    }

    private Supplier<IllegalArgumentException> illegal(String message) {
        return () -> new IllegalArgumentException(message);
    }
}
