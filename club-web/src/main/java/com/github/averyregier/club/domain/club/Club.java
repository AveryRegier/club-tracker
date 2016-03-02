package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/5/2014.
 */
public interface Club extends ClubGroup, Comparable<Club> {
    public Set<Policy> getPolicies();
    public Optional<Program> asProgram();
    public String getShortCode();
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role);
    public Curriculum getCurriculum();

    public Collection<AwardPresentation> getAwardsNotYetPresented(AccomplishmentLevel type);
    public Map<Clubber, Object> getClubNightReport();
}
