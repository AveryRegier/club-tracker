package com.github.averyregier.club.domain.program;

import java.util.Arrays;

/**
 * Created by avery on 9/6/2014.
 */
public interface AgeGroup {
    public enum DefaultAgeGroup implements AgeGroup {
        NURSERY,
        PRESCHOOL,
        KINDERGARTEN,
        FIRST_GRADE,
        SECOND_GRADE,
        THIRD_GRADE,
        FOURTH_GRADE,
        FIFTH_GRADE,
        SIXTH_GRADE,
        SEVENTH_GRADE,
        EIGHTH_GRADE,
        NINTH_GRADE,
        TENTH_GRADE,
        ELEVENTH_GRADE,
        TWELFTH_GRADE,
        COLLEGE,
        TWENTIES,
        THIRTIES,
        FORTIES,
        FIFTIES,
        SENIOR;

        @Override
        public String getDisplayName() {
            return Arrays.asList(name().split("_")).stream()
                    .map(s->s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase())
                    .reduce("", (a,b)->(a+' '+b).trim());
        }


    }

    public String getDisplayName();

}