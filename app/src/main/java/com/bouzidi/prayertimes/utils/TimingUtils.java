package com.bouzidi.prayertimes.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimingUtils {

    private static final String ADHAN_API_DEFAULT_FORMAT = "dd-MM-YYYY";

    public static boolean isAfterOrEqualsTiming(Date now, String endTiming) {
        String[] endParts = endTiming.split(":");
        Calendar endCal = Calendar.getInstance();

        endCal.setTime(now);
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);

        return nowCal.after(endCal) || nowCal.equals(endCal);
    }

    public static boolean isBeforeOrEqualsTiming(Date now, String endTiming, boolean endTimingAfterMidnight) {
        String[] endParts = endTiming.split(":");
        Calendar endCal = Calendar.getInstance();

        endCal.setTime(now);
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

        if (endTimingAfterMidnight) {
            endCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);

        return nowCal.before(endCal) || nowCal.equals(endCal);
    }

    public static boolean isStrictlyBeforeTiming(Date now, String endTiming) {
        String[] endParts = endTiming.split(":");
        Calendar endCal = Calendar.getInstance();

        endCal.setTime(now);
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);

        return nowCal.before(endCal);
    }

    public static boolean isBetweenTiming(String startTiming, Date now, String endTiming) {
        String[] startParts = startTiming.split(":");
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startParts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(startParts[1]));

        String[] endParts = endTiming.split(":");
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, nowCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, nowCal.get(Calendar.MINUTE));

        return (cal.after(startCal) && cal.before(endCal));
    }

    public static long getTimingBetween(String startTiming, String endTiming, boolean endTimingAfterMidnight) {
        String[] startParts = startTiming.split(":");
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startParts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(startParts[1]));

        String[] endParts = endTiming.split(":");
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

        if (endTimingAfterMidnight) {
            endCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return Math.abs(endCal.getTime().getTime() - startCal.getTime().getTime());
    }

    public static long getRemainingTiming(Date now, String endTiming, boolean endTimingAfterMidnight) {
        String[] endParts = endTiming.split(":");
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

        if (endTimingAfterMidnight) {
            endCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, nowCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, nowCal.get(Calendar.MINUTE));

        return Math.abs(endCal.getTime().getTime() - cal.getTime().getTime());
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

    public static String formatTimeForTimer(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return "- " + String.format(Locale.getDefault(), "%1$02d", Math.abs(hours)) + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(minutes % 60)) + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(seconds % 60));
    }

    public static String formatDate(int day, String monthName, int year) {
        return String.format(Locale.getDefault(), "%1$02d", day) + " " + monthName + ", " + year;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(ADHAN_API_DEFAULT_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }
}
