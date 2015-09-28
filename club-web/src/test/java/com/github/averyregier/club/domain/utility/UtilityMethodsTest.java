package com.github.averyregier.club.domain.utility;

import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Created by avery on 9/27/15.
 */
public class UtilityMethodsTest {

    @Test
    public void toSqlDate() {
        assertSqlDateCorrect(LocalDate.now());
        assertSqlDateCorrect(LocalDate.of(1970, 1, 1));
        assertSqlDateCorrect(LocalDate.of(1900, 1, 1));
        assertSqlDateCorrect(LocalDate.of(2999, 12, 31));

    }

    private void assertSqlDateCorrect(LocalDate now) {
        Date expected = new Date(now.getYear() - 1900, now.getMonth().getValue() - 1, now.getDayOfMonth());
        assertEquals(expected.toLocalDate(), UtilityMethods.toSqlDate(now).toLocalDate());
    }

}