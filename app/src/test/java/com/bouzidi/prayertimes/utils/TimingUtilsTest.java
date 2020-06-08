package com.bouzidi.prayertimes.utils;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimingUtilsTest {

    @Test
    public void isBeforeOrEqualsTiming() {
        assertTrue(TimingUtils.isBeforeOrEqualsTiming(getDate(2, 0), "03:00", false));
        assertTrue(TimingUtils.isBeforeOrEqualsTiming(getDate(22, 0), "00:05", true));
        assertFalse(TimingUtils.isBeforeOrEqualsTiming(getDate(22, 0), "10:30", false));
        assertFalse(TimingUtils.isBeforeOrEqualsTiming(getDate(14, 5), "14:03", false));
        assertTrue(TimingUtils.isBeforeOrEqualsTiming(getDate(0, 10), "00:05", true));
        assertTrue(TimingUtils.isBeforeOrEqualsTiming(getDate(20, 10), "20:10", false));
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