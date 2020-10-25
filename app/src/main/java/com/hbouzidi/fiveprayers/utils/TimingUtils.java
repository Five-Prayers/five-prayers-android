package com.hbouzidi.fiveprayers.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimingUtils {

    public static final String ADHAN_API_DEFAULT_FORMAT = "dd-MM-yyyy";
    public static final String LUT_API_DEFAULT_FORMAT = "yyyy-MM-dd";
    public static final String TIMING_DEFAULT_FORMAT = "HH:mm";

    public static LocalDateTime transformTimingToDate(String timing, String dateStr, boolean timingAfterMidnight) {
        String[] timingParts = getTimingParts(timing);

        DateTimeFormatter f = DateTimeFormatter.ofPattern(ADHAN_API_DEFAULT_FORMAT, Locale.getDefault());
        LocalDateTime localDateTime = LocalDate.parse(dateStr, f).
                atTime(Integer.parseInt(timingParts[0]), Integer.parseInt(timingParts[1]));

        if (timingAfterMidnight) {
            localDateTime = localDateTime.plusDays(1);
        }

        return localDateTime;
    }

    @NotNull
    private static String[] getTimingParts(String timingStr) {
        String[] result = new String[2];
        Pattern p = Pattern.compile("([0-9]{1,2}):([0-9]{1,2})");
        Matcher m = p.matcher(timingStr);

        if (m.find()) {
            result[0] = m.group(1);
            result[1] = m.group(2);
        }
        return result;
    }

    public static boolean isBetweenTiming(@NotNull LocalDateTime startTiming, @NotNull LocalDateTime now, @NotNull LocalDateTime endTiming) {
        return startTiming.equals(now) || (now.isAfter(startTiming) && now.isBefore(endTiming));
    }

    public static long getTimeBetween(LocalDateTime startTiming, LocalDateTime endTiming) {
        Duration dur = Duration.between(startTiming, endTiming);
        return Math.abs(dur.toMillis());
    }

    public static long getTimeBetweenTwoPrayer(LocalDateTime startTiming, LocalDateTime endTiming) {
        if (startTiming.isAfter(endTiming)) { //Mean that the next prayer is on the next day.
            endTiming = endTiming.plusDays(1);
        }
        Duration dur = Duration.between(startTiming, endTiming);
        return Math.abs(dur.toMillis());
    }

    public static boolean isBeforeOnSameDay(String startTiming, String endTiming) {
        String[] startParts = getTimingParts(startTiming);
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startParts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(startParts[1]));

        String[] endParts = getTimingParts(endTiming);
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

        return startCal.before(endCal);
    }

    public static long getTimeInMilliIgnoringSeconds(LocalDateTime localDateTime) {
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime.withSecond(0), ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public static String formatDateForAdhanAPI(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ADHAN_API_DEFAULT_FORMAT, Locale.getDefault());
        return localDate.format(formatter);
    }

    public static LocalDate parseAdhanAPIDate(String dateString) {
        DateTimeFormatter alAdhanDateFormatter = DateTimeFormatter.ofPattern(ADHAN_API_DEFAULT_FORMAT);
        return LocalDate.parse(dateString, alAdhanDateFormatter);
    }

    public static String formatDateForLUTAPI(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LUT_API_DEFAULT_FORMAT, Locale.getDefault());
        return localDate.format(formatter);
    }

    public static String formatTiming(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMING_DEFAULT_FORMAT, Locale.getDefault());
        return localDateTime.format(formatter);
    }

    public static LocalDate getLocalDateFromTimestamps(long timestamps) {
        return Instant.ofEpochSecond(timestamps).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static ZonedDateTime getZonedDateTimeFromTimestamps(long timestamps, String zoneName) {
        ZoneId zoneId = null;

        if (ZoneId.getAvailableZoneIds().contains(zoneName)) {
            zoneId = ZoneId.of(zoneName);
        }
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamps), (zoneId != null) ? zoneId : ZoneId.systemDefault());
    }

    public static String ConvertAlAdhanFormatToLUT(String aladhanDateString) {
        DateTimeFormatter alAdhanDateFormatter = DateTimeFormatter.ofPattern(ADHAN_API_DEFAULT_FORMAT);
        DateTimeFormatter lUTDateFormatter = DateTimeFormatter.ofPattern(LUT_API_DEFAULT_FORMAT);
        return LocalDate.parse(aladhanDateString, alAdhanDateFormatter).format(lUTDateFormatter);
    }
}
