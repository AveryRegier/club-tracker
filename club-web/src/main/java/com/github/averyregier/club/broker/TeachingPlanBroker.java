package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ScheduleRecord;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubMeeting;
import com.github.averyregier.club.domain.club.TeachingPlan;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.Scheduled;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Schedule.SCHEDULE;

public class TeachingPlanBroker extends PersistenceBroker<TeachingPlan> {
    public TeachingPlanBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(TeachingPlan thing, DSLContext create) {
        String clubId = thing.getSchedule().getContainer().getId();
        thing.getSchedule().getList().stream().forEach(s->{
            Optional<ClubMeeting> meeting = thing.getYear().getSchedule().getEventAt(s.getDate());
            if (meeting.isPresent() && create.insertInto(SCHEDULE)
                    .set(SCHEDULE.CLUB_ID, clubId.getBytes())
                    .set(SCHEDULE.MEETING_ID, meeting.get().getId().getBytes())
                    .set(SCHEDULE.CURRICULUM, thing.getCurriculum().getId())
                    .set(mapFields(thing, s))
                    .onDuplicateKeyUpdate()
                    .set(mapFields(thing, s))
                    .execute() != 1) {
                fail("Schedule persistence failed: " + clubId);
            }
        });
    }

    private Map<TableField<ScheduleRecord, ?>, Object> mapFields(
            TeachingPlan thing, Scheduled<Club, Section> sectionSchedule)
    {
        return JooqUtil.<ScheduleRecord>map()
                .set(SCHEDULE.SECTION_ID, sectionSchedule.getEvent().getId())
                .build();
    }
}
