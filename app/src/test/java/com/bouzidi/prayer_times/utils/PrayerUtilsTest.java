package com.bouzidi.prayer_times.utils;

import com.bouzidi.prayer_times.timings.Prayer;
import com.bouzidi.prayer_times.timings.PrayerEnum;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PrayerUtilsTest {

    @Test
    public void getNextPrayerIndex() {
        Prayer[] prayers = {
                new Prayer(PrayerEnum.FAJR, "03:28"),
                new Prayer(PrayerEnum.DHOHR, "12:59"),
                new Prayer(PrayerEnum.ASR, "16:55"),
                new Prayer(PrayerEnum.MAGHRIB, "20:12"),
                new Prayer(PrayerEnum.ICHA, "22:21")
        };

        //2h - FAJR
        assertEquals(0, PrayerUtils.getNextPrayerIndex(prayers, getDate(2, 0)));

        //3h29 - DHOHR
        assertEquals(1, PrayerUtils.getNextPrayerIndex(prayers, getDate(3, 29)));

        //13h00 - ASR
        assertEquals(2, PrayerUtils.getNextPrayerIndex(prayers, getDate(13, 0)));

        //16h56 - MAGHRIB
        assertEquals(3, PrayerUtils.getNextPrayerIndex(prayers, getDate(16, 56)));

        //22h05 - ICHA
        assertEquals(4, PrayerUtils.getNextPrayerIndex(prayers, getDate(22, 5)));

        //22h22 - ICHA
        assertEquals(-1, PrayerUtils.getNextPrayerIndex(prayers, getDate(22, 22)));
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