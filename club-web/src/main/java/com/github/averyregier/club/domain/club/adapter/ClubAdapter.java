package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Created by avery on 9/26/14.
*/
public abstract class ClubAdapter extends ClubGroupAdapter implements Club {
    private final Curriculum series;
    private Set<Clubber> clubbers;

    public ClubAdapter(Curriculum series) {
        this.series = series;
    }

    @Override
    public Set<Policy> getPolicies() {
        return null;
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
        if(clubbers == null) clubbers = initializeClubbers();
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
    public Collection<AwardPresentation> getAwardsNotYetPresented() {
        return getClubbers().stream()
                .flatMap(c->c.getAwards().stream())
                .filter(AwardPresentation::notPresented)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Clubber, Object> getClubNightReport() {
        LocalDate date = LocalDate.now();
        return getClubbers().stream()
                .collect(Collectors.toMap(Function.identity(),
                        c -> c.getRecords(
                                (r) -> r.getSigning()
                                        .map(s -> s.getDate().equals(date))
                                        .orElse(false))));
    }

    boolean accepts(ClubberAdapter clubber) {
        return getCurriculum().accepts(clubber.getCurrentAgeGroup());
    }

}
