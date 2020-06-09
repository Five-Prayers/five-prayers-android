package com.bouzidi.prayertimes.utils;

import java.util.Locale;

public class UiUtils {

    public static String formatTimeForTimer(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return "- " + String.format(Locale.getDefault(), "%1$02d", Math.abs(hours)) + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(minutes % 60)) + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(seconds % 60));
    }

    public static String formatHijriDate(int day, String monthName, int year) {
        return String.format(Locale.getDefault(), "%1$02d", day) + " " + monthName + ", " + year;
    }
}
