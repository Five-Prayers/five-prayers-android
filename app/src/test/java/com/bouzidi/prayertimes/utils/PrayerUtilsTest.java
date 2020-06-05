package com.bouzidi.prayertimes.utils;

import com.bouzidi.prayertimes.timings.PrayerEnum;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PrayerUtilsTest {

    @Test
    public void getNextPrayerIndex() {
        Map<PrayerEnum, String> prayers = new LinkedHashMap<>(5);
        prayers.put(PrayerEnum.FAJR, "03:28");
        prayers.put(PrayerEnum.DHOHR, "12:59");
        prayers.put(PrayerEnum.ASR, "16:55");
        prayers.put(PrayerEnum.MAGHRIB, "20:12");
        prayers.put(PrayerEnum.ICHA, "00:21");

        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate(2, 0)));

        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayers, getDate(3, 29)));

        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayers, getDate(13, 0)));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayers, getDate(16, 56)));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate(22, 5)));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate(22, 22)));
    }

    @Test
    public void getPreviousPrayerKey() {
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getPreviousPrayerKey(PrayerEnum.DHOHR));

        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getPreviousPrayerKey(PrayerEnum.ASR));

        assertEquals(PrayerEnum.ASR, PrayerUtils.getPreviousPrayerKey(PrayerEnum.MAGHRIB));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getPreviousPrayerKey(PrayerEnum.ICHA));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getPreviousPrayerKey(PrayerEnum.FAJR));
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