package com.bouzidi.prayertimes.timings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.bouzidi.prayertimes.database.PrayerRegistry;
import com.bouzidi.prayertimes.exceptions.TimingsException;
import com.bouzidi.prayertimes.timings.aladhan.AladhanAPIService;
import com.bouzidi.prayertimes.timings.aladhan.AladhanCalendarResponse;
import com.bouzidi.prayertimes.timings.aladhan.AladhanDate;
import com.bouzidi.prayertimes.timings.aladhan.AladhanTodayTimingsResponse;
import com.bouzidi.prayertimes.utils.TimingUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;

public class PrayerHelper {

    public static Single<DayPrayer> getTimingsByCity(final LocalDate localDate, final String city,
                                                     final String country,
                                                     final Context context) {

        CalculationMethodEnum method = getCalculationMethod(context);
        String tune = getTune(context);
        int latitudeAdjustmentMethod = getLatitudeAdjustmentMethod(context);
        int hijriAdjustment = getHijriAdjustment(context);

        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                if (localDate == null || city == null || country == null) {
                    Log.e(PrayerHelper.class.getName(), "Cannot find timings with null attribute");
                    emitter.onError(new TimingsException("Cannot find timings with null attributes"));
                } else {
                    String LocalDateString = TimingUtils.formatDateForAdhanAPI(localDate);
                    prayerTimings = prayerRegistry.getPrayerTimings(LocalDateString, city, country, method, latitudeAdjustmentMethod, hijriAdjustment, tune);

                    if (prayerTimings != null) {
                        emitter.onSuccess(prayerTimings);
                    } else {
                        try {
                            AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
                            AladhanTodayTimingsResponse timingsByCity = aladhanAPIService.getTimingsByCity(LocalDateString, city, country, method, latitudeAdjustmentMethod, hijriAdjustment, tune, context);
                            prayerRegistry.savePrayerTiming(LocalDateString, city, country, method, latitudeAdjustmentMethod, hijriAdjustment, tune, timingsByCity.getData());
                            prayerTimings = prayerRegistry.getPrayerTimings(LocalDateString, city, country, method, latitudeAdjustmentMethod, hijriAdjustment, tune);

                            emitter.onSuccess(prayerTimings);

                        } catch (IOException e) {
                            Log.e(PrayerHelper.class.getName(), "Cannot find from aladhanAPIService");
                            emitter.onError(e);
                        }
                    }
                }
            });
            thread.start();
        });
    }

    public static Single<List<AladhanDate>> getHijriCalendar(final int month,
                                                             final int year,
                                                             final Context context) {

        int hijriAdjustment = getHijriAdjustment(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();

                    emitter.onSuccess(aladhanAPIService
                            .getHijriCalendar(month, year, hijriAdjustment, context).getData());

                } catch (IOException e) {
                    Log.e(PrayerHelper.class.getName(), "Cannot find from aladhanAPIService");
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    public static Single<List<DayPrayer>> getCalendarByCity(final String city, final String country,
                                                            int month, int year,
                                                            final Context context) {

        CalculationMethodEnum method = getCalculationMethod(context);
        String tune = getTune(context);
        int latitudeAdjustmentMethod = getLatitudeAdjustmentMethod(context);
        int hijriAdjustment = getHijriAdjustment(context);

        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                List<DayPrayer> prayerCalendar;

                if (city == null || country == null) {
                    Log.e(PrayerHelper.class.getName(), "Cannot find calendar with null attribute");
                    emitter.onError(new TimingsException("Cannot find calendar with null attributes"));
                } else {
                    prayerCalendar = prayerRegistry.getPrayerCalendar(city, country, month, year, method, latitudeAdjustmentMethod, hijriAdjustment, tune);

                    if (prayerCalendar.size() == YearMonth.of(year, month).lengthOfMonth()) {
                        emitter.onSuccess(prayerCalendar);
                    } else {
                        try {
                            AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
                            AladhanCalendarResponse calendarByCity = aladhanAPIService.getCalendarByCity(city, country, month, year, method, latitudeAdjustmentMethod, hijriAdjustment, tune, context);
                            prayerRegistry.saveCalendar(city, country, method, latitudeAdjustmentMethod, hijriAdjustment, tune, calendarByCity);

                            prayerCalendar = prayerRegistry.getPrayerCalendar(city, country, month, year, method, latitudeAdjustmentMethod, hijriAdjustment, tune);

                            emitter.onSuccess(prayerCalendar);
                        } catch (IOException e) {
                            Log.e(PrayerHelper.class.getName(), "Cannot find calendar from aladhanAPIService");
                            emitter.onError(e);
                        }
                    }
                }
            });
            thread.start();
        });
    }

    private static CalculationMethodEnum getCalculationMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timingsCalculationMethodId = defaultSharedPreferences.getString("timings_calculation_method", String.valueOf(CalculationMethodEnum.getDefault()));

        return CalculationMethodEnum.valueOf(timingsCalculationMethodId);
    }

    private static String getTune(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("timing_adjustment", Context.MODE_PRIVATE);

        int fajrTimingAdjustment = sharedPreferences.getInt("fajr_timing_adjustment", 0);
        int dohrTimingAdjustment = sharedPreferences.getInt("dohr_timing_adjustment", 0);
        int asrTimingAdjustment = sharedPreferences.getInt("asr_timing_adjustment", 0);
        int maghrebTimingAdjustment = sharedPreferences.getInt("maghreb_timing_adjustment", 0);
        int ichaTimingAdjustment = sharedPreferences.getInt("icha_timing_adjustment", 0);

        return fajrTimingAdjustment + "," + fajrTimingAdjustment + ",0," + dohrTimingAdjustment + "," + asrTimingAdjustment + "," + maghrebTimingAdjustment + ",0," + ichaTimingAdjustment + ",0";
    }


    private static int getHijriAdjustment(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return defaultSharedPreferences.getInt("hijri_day_adjustment", 0);
    }

    private static int getLatitudeAdjustmentMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String latitudeAdjustmentMethod = defaultSharedPreferences.getString("timings_latitude_adjustment_method", "3");
        return Integer.parseInt(Objects.requireNonNull(latitudeAdjustmentMethod));
    }
}
