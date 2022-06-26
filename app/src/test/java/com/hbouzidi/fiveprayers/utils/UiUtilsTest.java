package com.hbouzidi.fiveprayers.utils;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class UiUtilsTest {

    @Test
    public void formatTimeForTimer() {
        String str = UiUtils.formatTimeForTimer(21767000);
        assertEquals("- 6:2:47", str);
    }

    @Test
    public void formatHijriDate() {
        String str = UiUtils.formatHijriDate(10, "Safar", 1415);
        assertNotNull(str);
    }

    @Test
    public void formatShortDate() {
        String str = UiUtils.formatShortDate(LocalDate.of(2020, 6, 18));
        assertNotNull(str);
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
        assertNotNull(str);
    }

    @Test
    public void formatReadableTimezone() {
        int timestamps = 1491379261;
        String str = UiUtils.formatReadableTimezone(TimingUtils.getZonedDateTimeFromTimestamps(timestamps, "Africa/Casablanca"));
        assertNotNull(str);
    }
}