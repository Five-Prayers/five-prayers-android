package com.hbouzidi.fiveprayers.timings;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import com.hbouzidi.fiveprayers.database.PrayerRegistry;
import com.hbouzidi.fiveprayers.exceptions.TimingsException;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanAPIService;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanCalendarResponse;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTodayTimingsResponse;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class DefaultTimingsService implements TimingsService {

    private static DefaultTimingsService instance;

    private DefaultTimingsService() {
    }

    public static DefaultTimingsService getInstance() {
        if (instance == null) {
            instance = new DefaultTimingsService();
        }
        return instance;
    }

    public Single<DayPrayer> getTimingsByCity(final LocalDate localDate,
                                              final Address address,
                                              final Context context) {

        CalculationMethodEnum method = PreferencesHelper.getCalculationMethod(context);
        String tune = PreferencesHelper.getTune(context);
        LatitudeAdjustmentMethod latitudeAdjustmentMethod = PreferencesHelper.getLatitudeAdjustmentMethod(context);
        SchoolAdjustmentMethod schoolAdjustmentMethod = PreferencesHelper.getSchoolAdjustmentMethod(context);
        MidnightModeAdjustmentMethod midnightModeAdjustmentMethod = PreferencesHelper.getMidnightModeAdjustmentMethod(context);
        int hijriAdjustment = PreferencesHelper.getHijriAdjustment(context);

        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                if (localDate == null || address == null) {
                    Log.e(DefaultTimingsService.class.getName(), "Cannot find timings with null attribute");
                    emitter.onError(new TimingsException("Cannot find timings with null attributes"));
                } else {
                    String LocalDateString = TimingUtils.formatDateForAdhanAPI(localDate);
                    prayerTimings = prayerRegistry.getPrayerTimings(LocalDateString, address.getLocality(), address.getCountryName(), method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune);

                    if (prayerTimings != null) {
                        emitter.onSuccess(prayerTimings);
                    } else {
                        try {
                            AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance(context);
                            AladhanTodayTimingsResponse timingsByCity = aladhanAPIService.getTimingsByLatLong(LocalDateString, address.getLatitude(), address.getLongitude(), method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune);
                            prayerRegistry.savePrayerTiming(LocalDateString, address.getLocality(), address.getCountryName(), method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune, timingsByCity.getData());
                            prayerTimings = prayerRegistry.getPrayerTimings(LocalDateString, address.getLocality(), address.getCountryName(), method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune);

                            emitter.onSuccess(prayerTimings);

                        } catch (IOException e) {
                            Log.e(DefaultTimingsService.class.getName(), "Cannot find from aladhanAPIService");
                            emitter.onError(e);
                        }
                    }
                }
            });
            thread.start();
        });
    }

    public Single<List<DayPrayer>> getCalendarByCity(
            final Address address,
            int month, int year,
            final Context context) {

        CalculationMethodEnum method = PreferencesHelper.getCalculationMethod(context);
        String tune = PreferencesHelper.getTune(context);
        LatitudeAdjustmentMethod latitudeAdjustmentMethod = PreferencesHelper.getLatitudeAdjustmentMethod(context);
        SchoolAdjustmentMethod schoolAdjustmentMethod = PreferencesHelper.getSchoolAdjustmentMethod(context);
        MidnightModeAdjustmentMethod midnightModeAdjustmentMethod = PreferencesHelper.getMidnightModeAdjustmentMethod(context);
        int hijriAdjustment = PreferencesHelper.getHijriAdjustment(context);

        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                List<DayPrayer> prayerCalendar;

                if (address == null) {
                    Log.e(DefaultTimingsService.class.getName(), "Cannot find calendar with null attribute");
                    emitter.onError(new TimingsException("Cannot find calendar with null attributes"));
                } else {
                    prayerCalendar = prayerRegistry.getPrayerCalendar(address.getLocality(), address.getCountryName(), month, year, method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune);

                    if (prayerCalendar.size() == YearMonth.of(year, month).lengthOfMonth()) {
                        emitter.onSuccess(prayerCalendar);
                    } else {
                        try {
                            AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance(context);
                            AladhanCalendarResponse calendarByCity = aladhanAPIService.getCalendarByLatLong(address.getLatitude(), address.getLongitude(), month, year, method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune);
                            prayerRegistry.saveCalendar(address.getLocality(), address.getCountryName(), method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune, calendarByCity);

                            prayerCalendar = prayerRegistry.getPrayerCalendar(address.getLocality(), address.getCountryName(), month, year, method, latitudeAdjustmentMethod, schoolAdjustmentMethod, midnightModeAdjustmentMethod, hijriAdjustment, tune);

                            emitter.onSuccess(prayerCalendar);
                        } catch (IOException e) {
                            Log.e(DefaultTimingsService.class.getName(), "Cannot find calendar from aladhanAPIService");
                            emitter.onError(e);
                        }
                    }
                }
            });
            thread.start();
        });
    }
}
