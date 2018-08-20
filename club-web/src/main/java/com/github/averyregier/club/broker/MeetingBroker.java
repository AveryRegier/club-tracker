package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.MeetingRecord;
import com.github.averyregier.club.domain.club.ClubMeeting;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Meeting.MEETING;

public class MeetingBroker extends PersistenceBroker<ClubMeeting> {
    public MeetingBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(ClubMeeting thing, DSLContext create) {
        if (create.insertInto(MEETING)
                .set(MEETING.ID, thing.getId().getBytes())
                .set(mapFields(thing))
                .onDuplicateKeyUpdate()
                .set(mapFields(thing))
                .execute() != 1) {
            fail("Meeting persistence failed: " + thing.getId());
        }
    }

    private Map<TableField<MeetingRecord, ?>, Object> mapFields(ClubMeeting thing) {
        return JooqUtil.<MeetingRecord>map()
                .set(MEETING.CLUB_YEAR_ID, thing.getClubYear())
                .set(MEETING.MEETING_DATE, thing.getDate())
                .build();
    }
}
