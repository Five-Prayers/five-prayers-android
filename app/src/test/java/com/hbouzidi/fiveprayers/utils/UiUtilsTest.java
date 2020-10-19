package com.hbouzidi.fiveprayers.utils;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class UiUtilsTest {

    @Test
    public void formatTimeForTimer() {
        String str = UiUtils.formatTimeForTimer(21767000);
        assertEquals("- 06:02:47", str);
    }

    @Test
    public void formatHijriDate() {
        String str = UiUtils.formatHijriDate(10, "Safar", 1415);
        assertEquals("10 Safar, 1415", str);
    }

    @Test
    public void formatShortDate() {
        String str = UiUtils.formatShortDate(LocalDate.of(2020, 6, 18));
        assertEquals("juin 2020", str);
    }

    @Test
    public void formatShortHijriDate() {
        String str = UiUtils.formatShortHijriDate(10, "Safar");
        assertEquals("10 Safar", str);
    }

    @Test
    public void formatReadableGregorianDate() {
        int timestamps = 1491379261;
        String str = UiUtils.formatReadableGregorianDate(TimingUtils.getZonedDateTimeFromTimestamps(timestamps, "Africa/Casablanca"));
        assertEquals("mer. 05 avril, 2017", str);
    }

    @Test
    public void formatReadableTimezone() {
        int timestamps = 1491379261;
        String str = UiUtils.formatReadableTimezone(TimingUtils.getZonedDateTimeFromTimestamps(timestamps, "Africa/Casablanca"));
        assertEquals("+01:00", str);
    }
}