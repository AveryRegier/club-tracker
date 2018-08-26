package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ScheduleRecord;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubMeeting;
import com.github.averyregier.club.domain.club.TeachingPlan;
import com.github.averyregier.club.domain.club.adapter.TeachingPlanAdapter;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.HasDate;
import com.github.averyregier.club.domain.utility.Schedule;
import com.github.averyregier.club.domain.utility.Scheduled;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.TableField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Schedule.SCHEDULE;
import static com.github.averyregier.club.domain.utility.UtilityMethods.stream;

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

    public Map<Curriculum, TeachingPlan> find(Club club) {
        return query(create -> create
                .select(SCHEDULE.CURRICULUM, SCHEDULE.MEETING_ID, SCHEDULE.SECTION_ID)
                .from(SCHEDULE)
                .where(SCHEDULE.CLUB_ID.eq(club.getId().getBytes()))
                .fetch().stream()
                .collect(Collectors.groupingBy(Record3::value1)))
                .entrySet().stream()
                .map(e -> map(club, e.getKey(), e.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(TeachingPlan::getCurriculum, Function.identity()));
    }

    public Optional<TeachingPlan> map(Club club, String curriculum, List<Record3<String, byte[], String>> records) {
        Optional<Curriculum> first = club.getScheduledCurriculum().stream()
                .filter(c -> c.getId().equals(curriculum)).findFirst();

        List<Scheduled<Club, Section>> sectionSchedule = map(club, records);
        Schedule<Club, Section> schedule = new Schedule<>(club, sectionSchedule);

        Optional<ClubMeeting> clubMeeting = records.stream().findFirst()
                .flatMap(r -> getClubMeeting(club, r.value2()));

        return first.flatMap(c->clubMeeting.map(ClubMeeting::getClubYear)
                .map(clubYear->new TeachingPlanAdapter(c, schedule, clubYear)));
    }

    public List<Scheduled<Club, Section>> map(Club club, List<Record3<String, byte[], String>> records) {
        return records.stream()
                    .flatMap(r -> stream(club.getCurriculum().lookup(r.value3()))
                        .flatMap(s -> stream(getMeetingDate(club, r.value2()))
                            .map(d->new Scheduled<>(club, d, s))))
                    .collect(Collectors.toList());
    }

    private Optional<LocalDate> getMeetingDate(Club club, byte[] bytes) {
        return getClubMeeting(club, bytes)
                .map(HasDate::getDate);
    }

    private Optional<ClubMeeting> getClubMeeting(Club club, byte[] bytes) {
        return club.getProgram().getMeetings().stream()
                .filter(m-> Arrays.equals(m.getId().getBytes(), bytes))
                .findFirst();
    }
}
