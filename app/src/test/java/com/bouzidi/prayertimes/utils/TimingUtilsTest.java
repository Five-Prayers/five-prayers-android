package com.bouzidi.prayertimes.utils;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimingUtilsTest {

    @Test
    public void isBeforeTiming() {
        assertTrue(TimingUtils.isBeforeTiming(getDate(2, 0), "03:00", false));
        assertTrue(TimingUtils.isBeforeTiming(getDate(22, 0), "00:05", true));
        assertFalse(TimingUtils.isBeforeTiming(getDate(22, 0), "10:30", false));
        assertFalse(TimingUtils.isBeforeTiming(getDate(14, 5), "14:03", false));
        assertTrue(TimingUtils.isBeforeTiming(getDate(0, 10), "00:05", true));
    }

    @NotNull
    private Date getDate(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }
}