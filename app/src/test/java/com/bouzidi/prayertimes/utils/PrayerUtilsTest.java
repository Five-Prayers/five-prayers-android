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
    public void getNextPrayer() {
        Map<PrayerEnum, String> prayers = new LinkedHashMap<>(5);
        prayers.put(PrayerEnum.FAJR, "03:28");
        prayers.put(PrayerEnum.DHOHR, "12:59");
        prayers.put(PrayerEnum.ASR, "16:55");
        prayers.put(PrayerEnum.MAGHRIB, "20:12");
        prayers.put(PrayerEnum.ICHA, "00:21");

        //Icha is after midnight
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate(0, 0)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate(0, 21)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate(2, 0)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayers, getDate(3, 28)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayers, getDate(3, 29)));

        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayers, getDate(12, 58)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayers, getDate(12, 59)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayers, getDate(13, 0)));

        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayers, getDate(16, 54)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayers, getDate(16, 55)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayers, getDate(16, 56)));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayers, getDate(18, 0)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate(20, 12)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate(20, 13)));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate(23, 30)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate(0, 10)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate(0, 21)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate(0, 55)));

        //Icha is before midnight
        Map<PrayerEnum, String> prayerTwo = new LinkedHashMap<>(5);
        prayerTwo.put(PrayerEnum.FAJR, "04:13");
        prayerTwo.put(PrayerEnum.DHOHR, "13:50");
        prayerTwo.put(PrayerEnum.ASR, "18:06");
        prayerTwo.put(PrayerEnum.MAGHRIB, "21:52");
        prayerTwo.put(PrayerEnum.ICHA, "23:27");

        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate(23, 42)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate(0, 30)));

        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate(4, 0)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayerTwo, getDate(4, 13)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayerTwo, getDate(5, 42)));

        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayerTwo, getDate(12, 0)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayerTwo, getDate(13, 50)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayerTwo, getDate(18, 5)));

        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayerTwo, getDate(17, 23)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerTwo, getDate(18, 6)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerTwo, getDate(20, 5)));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerTwo, getDate(21, 0)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerTwo, getDate(21, 52)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerTwo, getDate(21, 53)));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerTwo, getDate(23, 26)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate(23, 27)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate(0, 0)));
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