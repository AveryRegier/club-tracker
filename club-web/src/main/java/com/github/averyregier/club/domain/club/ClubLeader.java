package com.github.averyregier.club.domain.club;

import java.util.Random;

/**
 * Created by avery on 9/5/2014.
 */
public interface ClubLeader extends ClubWorker {
    enum LeadershipRole {
        KIOSK {
            @Override
            public boolean isKiosk() {
                return true;
            }
        },
        SECRETARY,
        DIRECTOR,
        COMMANDER,
        PASTOR;

        private static Random random = new Random();

        public static LeadershipRole random() {
            LeadershipRole[] values = values();
            return values[random.nextInt(values.length)];
        }

        public boolean isKiosk() {
            return false;
        }
    }
    Program getProgram();
    LeadershipRole getLeadershipRole();
}
