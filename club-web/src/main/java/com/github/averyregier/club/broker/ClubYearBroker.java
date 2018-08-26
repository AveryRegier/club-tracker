package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubYearRecord;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubMeeting;
import com.github.averyregier.club.domain.club.ClubYear;
import com.github.averyregier.club.domain.club.adapter.ClubYearAdapter;
import com.github.averyregier.club.domain.utility.Schedule;
import com.github.averyregier.club.domain.utility.Scheduled;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.TableField;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.ClubYear.CLUB_YEAR;

public class ClubYearBroker extends PersistenceBroker<ClubYear> {
    public ClubYearBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(ClubYear thing, DSLContext create) {
        if (create.insertInto(CLUB_YEAR)
                .set(CLUB_YEAR.ID, thing.getId().getBytes())
                .set(mapFields(thing))
                .onDuplicateKeyUpdate()
                .set(mapFields(thing))
                .execute() != 1) {
            fail("Club Year persistence failed: " + thing.getId());
        }
    }

    private Map<TableField<ClubYearRecord, ?>, Object> mapFields(ClubYear thing) {
        return JooqUtil.<ClubYearRecord>map()
                .set(CLUB_YEAR.CLUB_ID, thing.getClub())
                .set(CLUB_YEAR.NAME, thing.getClubYear())
                .build();
    }

    public List<ClubYear> find(Club club) {
        return query(create->create
            .select(CLUB_YEAR.ID, CLUB_YEAR.NAME)
                .from(CLUB_YEAR)
                .where(CLUB_YEAR.CLUB_ID.eq(club.getId().getBytes()))
                .fetch().stream()
                .map(r-> new PersistedClubYear(club, r))
                .peek(cy->cy.setSchedule(new MeetingBroker(connector).find(cy)))
                .collect(Collectors.toList())
        );
    }

    private static class PersistedClubYear extends ClubYearAdapter {
        public PersistedClubYear(Club club, Record2<byte[], String> r) {
            super(club, new String(r.value1()), r.value2());
        }

        protected void setSchedule(List<ClubMeeting> meetings) {
            List<Scheduled<ClubYear, ClubMeeting>> schedule = meetings.stream()
                    .map(m -> new Scheduled<ClubYear, ClubMeeting>(this, m.getDate(), m))
                    .collect(Collectors.toList());
            super.setSchedule(new Schedule<>(this, schedule));
        }
    }
}
