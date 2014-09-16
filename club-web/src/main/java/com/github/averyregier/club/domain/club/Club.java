package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.Optional;
import java.util.Set;

/**
 * Created by avery on 9/5/2014.
 */
public interface Club extends ClubGroup {
    public Set<Policy> getPolicies();
    public ClubType getClubType();
    public Optional<Program> asProgram();
    public String getShortName();
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role);
    public Curriculum getCurriculum();

}
