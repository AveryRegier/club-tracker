package com.github.averyregier.club.domain.club;

/**
 * Created by rx39789 on 9/5/2014.
 */
public interface ClubLeader extends ClubWorker {
    public enum LeadershipRole {
        SECRETARY,
        DIRECTOR,
        COMMANDER,
        PASTOR;
    }

    public Program getProgram();
}
