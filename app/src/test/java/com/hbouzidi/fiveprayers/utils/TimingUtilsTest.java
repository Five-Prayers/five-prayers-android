package com.hbouzidi.fiveprayers.utils;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class TimingUtilsTest {

    @Test
    public void transformTimingToDate() {
        LocalDateTime localDateTime = TimingUtils.transformTimingToDate("12:50 (BST)", "13-05-2020", false);
        assertTrue(localDateTime.isEqual(LocalDateTime.of(2020, 5, 13, 12, 50)));

        LocalDateTime localDateTime2 = TimingUtils.transformTimingToDate("12:50", "13-05-2020", false);
        assertTrue(localDateTime2.isEqual(LocalDateTime.of(2020, 5, 13, 12, 50)));

        LocalDateTime localDateTime3 = TimingUtils.transformTimingToDate("04:45", "13-05-2020", false);
        assertTrue(localDateTime3.isEqual(LocalDateTime.of(2020, 5, 13, 4, 45)));

        LocalDateTime localDateTimeAfterMidnight = TimingUtils.transformTimingToDate("12:50", "13-05-2020", true);
        assertTrue(localDateTimeAfterMidnight.isEqual(LocalDateTime.of(2020, 5, 14, 12, 50)));

        LocalDateTime localDateTimeAfterMidnight2 = TimingUtils.transformTimingToDate("12:50 (EST)", "13-05-2020", true);
        assertTrue(localDateTimeAfterMidnight2.isEqual(LocalDateTime.of(2020, 5, 14, 12, 50)));
    }

    @Test
    public void isBetweenTiming() {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 13, 12, 50);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 14, 5, 30);

        LocalDateTime targetDate1 = LocalDateTime.of(2020, 5, 13, 12, 50);
        LocalDateTime targetDate2 = LocalDateTime.of(2020, 5, 14, 5, 30);
        LocalDateTime targetDate3 = LocalDateTime.of(2020, 5, 13, 12, 40);
        LocalDateTime targetDate4 = LocalDateTime.of(2020, 5, 14, 5, 40);
        LocalDateTime targetDate5 = LocalDateTime.of(2020, 5, 14, 4, 10);

        assertTrue(TimingUtils.isBetweenTiming(startTime, targetDate1, endTime));
        assertFalse(TimingUtils.isBetweenTiming(startTime, targetDate2, endTime));
        assertFalse(TimingUtils.isBetweenTiming(startTime, targetDate3, endTime));
        assertFalse(TimingUtils.isBetweenTiming(startTime, targetDate4, endTime));
        assertTrue(TimingUtils.isBetweenTiming(startTime, targetDate5, endTime));
    }

    @Test
    public void getTimeBetween() {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 14, 4, 50);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 14, 5, 30);

        assertEquals(TimingUtils.getTimeBetween(startTime, endTime), 1000 * 60 * 40);
        assertEquals(TimingUtils.getTimeBetween(endTime, startTime), 1000 * 60 * 40);
    }

    @Test
    public void getTimeBetweenTwoPrayer() {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 14, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 14, 3, 0);

        assertEquals(1000 * 60 * 190, TimingUtils.getTimeBetweenTwoPrayer(startTime, endTime));
        assertEquals(TimingUtils.getTimeBetweenTwoPrayer(endTime, startTime), 1000 * 60 * 1250);
    }

    @Test
    public void isBeforeOnSameDay() {
        assertTrue(TimingUtils.isBeforeOnSameDay("00:15", "15:12"));
        assertTrue(TimingUtils.isBeforeOnSameDay("00:15", "23:40"));
        assertFalse(TimingUtils.isBeforeOnSameDay("10:15", "9:12"));
    }

    @Test
    public void getTimeInMilliseconds() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.of(2020, 5, 14, 4, 50, 55);
        long timeInMilliseconds = TimingUtils.getTimeInMilliIgnoringSeconds(localDateTime);
        Date date = new Date(timeInMilliseconds);

        assertNotEquals(simpleDateFormat.format(date), "14-05-2020 04:50:55");

        LocalDateTime localDateTime2 = LocalDateTime
                .of(2020, 5, 14, 4, 50, 55)
                .withSecond(0);

        long timeInMilliseconds2 = TimingUtils.getTimeInMilliIgnoringSeconds(localDateTime2);
        Date date2 = new Date(timeInMilliseconds2);

        assertEquals(simpleDateFormat.format(date2), "14-05-2020 04:50:00");
    }


    @Test
    public void formatDateForAdhanAPI() {
        String formatDateForAdhanAPI = TimingUtils.formatDateForAdhanAPI(LocalDate.of(2020, 10, 14));

        assertEquals("14-10-2020", formatDateForAdhanAPI);
        assertNotEquals("10-14-2020", formatDateForAdhanAPI);

    }

    @Test
    public void formatTiming() {
        LocalDateTime localDateTime = LocalDateTime.of(2020, 5, 14, 4, 50);
        assertEquals("04:50", TimingUtils.formatTiming(localDateTime));

        LocalDateTime localDateTime2 = LocalDateTime.of(2020, 5, 14, 4, 50);
        assertEquals("04:50", TimingUtils.formatTiming(localDateTime2));
    }

    @Test
    public void getLocalDateFromTimestamps() {
        int timestamps = 1491379261;
        LocalDate localDate = TimingUtils.getLocalDateFromTimestamps(timestamps);

        assertEquals(5, localDate.getDayOfMonth());
        assertEquals(4, localDate.getMonthValue());
        assertEquals(2017, localDate.getYear());
    }

    @Test
    public void getLocalDateTimeFromTimestamps() {
        int timestamps = 1491379261;
        ZonedDateTime zonedDateTime = TimingUtils.getZonedDateTimeFromTimestamps(timestamps, "Africa/Casablanca");
        assertEquals(9, zonedDateTime.getHour());
        assertEquals(1, zonedDateTime.getMinute());
        assertEquals(5, zonedDateTime.getDayOfMonth());
        assertEquals(4, zonedDateTime.getMonthValue());
        assertEquals(2017, zonedDateTime.getYear());
    }

    @Test
    public void convertToLocalDateTime() throws ParseException {
        String sDate = "31/12/1998 23:37:50";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = simpleDateFormat.parse(sDate);

        LocalDateTime localDateTime = TimingUtils.convertToLocalDateTime(date, ZoneId.of("Europe/Paris"));

        assertEquals(0, localDateTime.getHour());
        assertEquals(37, localDateTime.getMinute());
        assertEquals(50, localDateTime.getSecond());
        assertEquals(1, localDateTime.getDayOfMonth());
        assertEquals(1, localDateTime.getMonthValue());
        assertEquals(1999, localDateTime.getYear());
    }

    @Test
    public void getTimestamps()  {
        long timestamps = TimingUtils.getTimestampsFromLocalDateTime(LocalDateTime.of(2021, 4, 25, 19, 7), ZoneId.of("Europe/Paris"));

        assertEquals(1619370420, timestamps);
    }

    @Test
    public void getLastThirdOfTheNight() {
        LocalDateTime fajr1 = LocalDateTime.of(2021, 5, 13, 3, 0);
        LocalDateTime maghrib1 = LocalDateTime.of(2021, 5, 13, 19, 0);

        LocalDateTime lastThirdOfTheNight1 = TimingUtils.getLastThirdOfTheNight(fajr1, maghrib1);
        Assertions.assertThat(lastThirdOfTheNight1).isEqualTo(LocalDateTime.of(2021, 5, 13, 0, 20));

        LocalDateTime fajr2 = LocalDateTime.of(2021, 5, 13, 2, 0);
        LocalDateTime maghrib2 = LocalDateTime.of(2021, 5, 13, 18, 0);

        LocalDateTime lastThirdOfTheNight2 = TimingUtils.getLastThirdOfTheNight(fajr2, maghrib2);
        Assertions.assertThat(lastThirdOfTheNight2).isEqualTo(LocalDateTime.of(2021, 5, 13, 23, 20));

        LocalDateTime fajr3 = LocalDateTime.of(2021, 5, 13, 2, 0);
        LocalDateTime maghrib3 = LocalDateTime.of(2021, 5, 13, 21, 0);

        LocalDateTime lastThirdOfTheNight3 = TimingUtils.getLastThirdOfTheNight(fajr3, maghrib3);
        Assertions.assertThat(lastThirdOfTheNight3).isEqualTo(LocalDateTime.of(2021, 5, 13, 0, 20));

        LocalDateTime fajr4 = LocalDateTime.of(2021, 5, 13, 6, 0);
        LocalDateTime maghrib4 = LocalDateTime.of(2021, 5, 13, 18, 0);

        LocalDateTime lastThirdOfTheNight4 = TimingUtils.getLastThirdOfTheNight(fajr4, maghrib4);
        Assertions.assertThat(lastThirdOfTheNight4).isEqualTo(LocalDateTime.of(2021, 5, 13, 2, 0));
    }
}