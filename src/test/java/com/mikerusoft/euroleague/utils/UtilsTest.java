package com.mikerusoft.euroleague.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {

    @Test
    @DisplayName("when beginning of year, a.k.k.a January of 2020 expected seasnon 20192020")
    void whenBeginningOfYear_expectedSeasonLastYearAndCurrent() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.set(2020, Calendar.JANUARY, 1, 11, 10, 0);
        Date date = gmt.getTime();
        assertThat(Utils.extractSeason(date)).isNotNull().isEqualTo("20192020");
    }

    @Test
    @DisplayName("when beginning of year, a.k.k.a July of 2020 expected season 20202021")
    void whenAfterJulyOfYear_expectedSeasonNextYearAndAfterNext() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.set(2020, Calendar.JULY, 1, 11, 10, 0);
        Date date = gmt.getTime();
        assertThat(Utils.extractSeason(date)).isNotNull().isEqualTo("20202021");
    }
}