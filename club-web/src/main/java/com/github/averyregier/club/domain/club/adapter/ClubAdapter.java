package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.Schedule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.github.averyregier.club.domain.utility.UtilityMethods.optMap;
import static com.github.averyregier.club.domain.utility.UtilityMethods.stream;

/**
 * Created by avery on 9/26/14.
 */
public abstract class ClubAdapter extends ClubGroupAdapter implements Club {
    private final Curriculum series;
    private Set<Clubber> clubbers;
    private ConcurrentMap<Curriculum, Schedule<Club, Section>> schedules =
            new ConcurrentHashMap<>();

    public ClubAdapter(Curriculum series) {
        this.series = series;
    }

    @Override
    public Optional<Program> asProgram() {
        return Optional.empty();
    }

    @Override
    public String getShortCode() {
        return series.getShortCode();
    }

    @Override
    public String getName() {
        return series.getName();
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Curriculum getCurriculum() {
        return series;
    }

    @Override
    public Optional<ClubGroup> getParentGroup() {
        return Optional.of(getProgram());
    }

    @Override
    public Optional<Club> asClub() {
        return Optional.of(this);
    }

    @Override
    public Set<Clubber> getClubbers() {
        ensureClubbersInitialized();
        return Collections.unmodifiableSet(new TreeSet<>(clubbers));
    }

    private synchronized void ensureClubbersInitialized() {
        if (clubbers == null) clubbers = initializeClubbers();
    }

    protected HashSet<Clubber> initializeClubbers() {
        return new HashSet<>();
    }


    @Override
    public int compareTo(Club o) {
        return getShortCode().compareTo(o.getShortCode());
    }

    void addClubber(ClubberAdapter clubber) {
        ensureClubbersInitialized();
        this.clubbers.add(clubber);
        clubber.setClub(this);
    }

    @Override
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
        return new ClubLeaderAdapter(person, role, this);
    }

    @Override
    public boolean isLeader(Person person) {
        return stream(Optional.of(this), ClubGroup::getParentGroup)
                .anyMatch(c -> c.equals(optMap(person.asClubLeader(), ClubMember::getClub).orElse(null)));
    }

    boolean accepts(ClubberAdapter clubber) {
        return getCurriculum().accepts(clubber.getCurrentAgeGroup());
    }

    @Override
    public void setSchedule(TeachingPlan teachingPlan) {
        schedules.put(teachingPlan.getCurriculum(), teachingPlan.getSchedule());
        persist(teachingPlan);
    }

    protected void persist(TeachingPlan teachingPlan) { }

    @Override
    public Optional<Schedule<Club, Section>> getSchedule(Curriculum curriculum) {
        return Optional.ofNullable(schedules.get(curriculum));
    }
}
