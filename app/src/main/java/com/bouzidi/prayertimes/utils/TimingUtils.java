package com.bouzidi.prayertimes.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TimingUtils {

    public static final String ADHAN_API_DEFAULT_FORMAT = "dd-MM-yyyy";
    public static final String TIMING_DEFAULT_FORMAT = "hh:mm";

    public static LocalDateTime transformTimingToDate(String timing, String dateStr, boolean timingAfterMidnight) {
        String[] timingParts = timing.split(":");

        DateTimeFormatter f = DateTimeFormatter.ofPattern(ADHAN_API_DEFAULT_FORMAT, Locale.getDefault());
        LocalDateTime localDateTime = LocalDate.parse(dateStr, f).
                atTime(Integer.parseInt(timingParts[0]), Integer.parseInt(timingParts[1]));

        if (timingAfterMidnight) {
            localDateTime = localDateTime.plusDays(1);
        }

        return localDateTime;
    }

    public static boolean isBetweenTiming(@NotNull LocalDateTime startTiming, @NotNull LocalDateTime now, @NotNull LocalDateTime endTiming) {
        return startTiming.equals(now) || (now.isAfter(startTiming) && now.isBefore(endTiming));
    }

    public static long getTimeBetween(LocalDateTime startTiming, LocalDateTime endTiming) {
        Duration dur = Duration.between(startTiming, endTiming);
        return Math.abs(dur.toMillis());
    }

    public static long getTimeBetweenTwoPrayer(LocalDateTime startTiming, LocalDateTime endTiming) {
        if (startTiming.isAfter(endTiming)) {
            endTiming = endTiming.plusDays(1);
        }
        Duration dur = Duration.between(startTiming, endTiming);
        return Math.abs(dur.toMillis());
    }

    public static boolean isBeforeOnSameDay(String startTiming, String endTiming) {
        String[] startParts = startTiming.split(":");
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startParts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(startParts[1]));

        String[] endParts = endTiming.split(":");
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

    public static String formatTiming(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMING_DEFAULT_FORMAT, Locale.getDefault());
        return localDateTime.format(formatter);
    }
}
