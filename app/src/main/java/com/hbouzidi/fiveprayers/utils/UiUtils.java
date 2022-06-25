package com.hbouzidi.fiveprayers.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class UiUtils {

    public static final String GREGORIAN_MONTH_YEAR_FORMAT = "MMMM yyyy";
    public static final String TIME_ZONE_READABLE_FORMAT = "ZZZZZ";

    public static String formatTimeForTimer(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return "- " + String.format(Locale.getDefault(), "%1$02d", Math.abs(hours)).replaceFirst("^[0٠]+(?!$)", "") + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(minutes % 60)).replaceFirst("^[0٠]+(?!$)", "") + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(seconds % 60)).replaceFirst("^[0٠]+(?!$)", "");
    }

    public static String formatTimeForWidgetTimer(long time, String hoursSeparator, String minutesSeparator) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return String.format(Locale.getDefault(), "%1$02d", Math.abs(hours)).replaceFirst("^[0٠]+(?!$)", "") + hoursSeparator + String.format(Locale.getDefault(), "%1$02d", Math.abs(minutes % 60)).replaceFirst("^[0٠]+(?!$)", "") + minutesSeparator;
    }

    public static String formatShortDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(GREGORIAN_MONTH_YEAR_FORMAT, Locale.getDefault())
                .withLocale(Locale.getDefault());

        try {
            return localDate.format(formatter.withDecimalStyle(DecimalStyle.of(Locale.getDefault())));
        } catch (UnsupportedOperationException e) {
            return localDate.format(formatter);
        }
    }

    public static String formatTiming(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault());

        try {
            return zdt.format(formatter.withDecimalStyle(DecimalStyle.of(Locale.getDefault())));
        } catch (UnsupportedOperationException e) {
            return zdt.format(formatter);
        }
    }

    public static String formatReadableGregorianDate(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL)
                .withLocale(Locale.getDefault());

        try {
            return zonedDateTime.toLocalDate()
                    .format(formatter.withDecimalStyle(DecimalStyle.of(Locale.getDefault()))).replaceAll("[٬،.]", "");
        } catch (UnsupportedOperationException e) {
            return zonedDateTime.toLocalDate().format(formatter).replaceAll("[٬،.]", "");
        }
    }

    public static String formatMediumReadableGregorianDate(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.getDefault());

        try {
            return zonedDateTime.toLocalDate()
                    .format(formatter.withDecimalStyle(DecimalStyle.of(Locale.getDefault()))).replaceAll("[٬،.]", "");
        } catch (UnsupportedOperationException e) {
            return zonedDateTime.toLocalDate().format(formatter).replaceAll("[٬،.]", "");
        }
    }

    public static String formatReadableGregorianDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL)
                .withLocale(Locale.getDefault());

        try {
            return localDate.format(formatter.withDecimalStyle(DecimalStyle.of(Locale.getDefault()))).replaceAll("[٬،.]", "");
        } catch (UnsupportedOperationException e) {
            return localDate.format(formatter).replaceAll("[٬،.]", "");
        }
    }

    public static String formatReadableTimezone(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(TIME_ZONE_READABLE_FORMAT, Locale.getDefault())
                .withZone(ZoneId.systemDefault())
                .withLocale(Locale.getDefault());
        try {
            return zonedDateTime.format(formatter.withDecimalStyle(DecimalStyle.of(Locale.getDefault())));
        } catch (UnsupportedOperationException e) {
            return zonedDateTime.format(formatter);
        }
    }

    public static String formatFullHijriDate(String nameOfTheDay, int day, String monthName, int year) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        String date = nameOfTheDay + " " + numberFormat.format(day) + " " + monthName + " " + numberFormat.format(year);
        return date.replaceAll("[٬،.,]", "");
    }

    public static String formatHijriDate(int day, String monthName, int year) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        String date = numberFormat.format(day) + " " + monthName + " " + numberFormat.format(year);
        return date.replaceAll("[٬،.,]", "");
    }

    public static String formatShortHijriDate(int day, String monthName) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        String date = numberFormat.format(day) + " " + monthName;
        return date.replaceAll("[٬،.,]", "");
    }

    public static Uri uriFromRaw(String name, Context context) {
        Resources res = context.getResources();
        int mediaId = res.getIdentifier(name.toLowerCase(), "raw",
                context.getPackageName());

        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + mediaId);
    }
}
