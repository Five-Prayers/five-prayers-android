package com.hbouzidi.fiveprayers.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.hbouzidi.fiveprayers.R;

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

        return "- " + String.format(Locale.getDefault(), "%1$02d", Math.abs(hours)) + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(minutes % 60)) + ":" + String.format(Locale.getDefault(), "%1$02d", Math.abs(seconds % 60));
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
        return formatReadableGregorianDate(zonedDateTime.toLocalDate(), FormatStyle.FULL);
    }

    public static String formatMediumReadableGregorianDate(ZonedDateTime zonedDateTime) {
        return formatReadableGregorianDate(zonedDateTime.toLocalDate(), FormatStyle.LONG);
    }

    public static String formatReadableGregorianDate(LocalDate localDate, FormatStyle dateStyle) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDate(dateStyle)
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

    public static Bitmap textToBitmap(String text,
                                      int textSize, int fontId,
                                      @ColorRes int textColorRes,
                                      @ColorRes int textShadowColorRes,
                                      @ColorRes int backgroundColorRes, Context context) {

        Typeface font = ResourcesCompat.getFont(context, fontId);

        // prepare canvas
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;

        // new antialiased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(ContextCompat.getColor(context, textColorRes));
        // text size in pixels
        paint.setTextSize((int) (textSize * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, ContextCompat.getColor(context, textShadowColorRes));
        paint.setTypeface(font);
        paint.setTextAlign(Paint.Align.LEFT);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // init StaticLayout for text
        StaticLayout textLayout;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textLayout = StaticLayout.Builder
                    .obtain(text, 0, text.length(), paint, (int) (size.x * 0.8))
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setTextDirection(TextDirectionHeuristics.RTL)
                    .build();
        } else {
            textLayout = new StaticLayout(
                    text, paint, (int) (size.x * 0.8), Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }

        Bitmap bitmap = Bitmap.createBitmap(textLayout.getWidth(), textLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(context, backgroundColorRes));

        // set text width to canvas width minus 16dp padding
        int textWidth = textLayout.getWidth();

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        int x = (bitmap.getWidth() - textWidth) / 2;
        int y = (bitmap.getHeight() - textHeight) / 2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    public static String convertAndFormatCoordinates(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append(" ");

        if (longitude < 0) {
            builder.append("W ");
        } else {
            builder.append("E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }

    public static String getIslamicPhrase(String text, Context context) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        Typeface typeface = ResourcesCompat.getFont(context, R.font.aga_islamic_phrases);
        builder.append(text);
        builder.setSpan(new CustomTypefaceSpan(typeface), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(2.5f), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder.toString();
    }
}
