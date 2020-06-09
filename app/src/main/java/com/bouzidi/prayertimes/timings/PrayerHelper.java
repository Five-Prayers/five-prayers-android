package com.bouzidi.prayertimes.timings;

import android.content.Context;
import android.util.Log;

import com.bouzidi.prayertimes.database.PrayerRegistry;
import com.bouzidi.prayertimes.exceptions.TimingsException;
import com.bouzidi.prayertimes.timings.aladhan.AladhanAPIService;
import com.bouzidi.prayertimes.timings.aladhan.AladhanTodayTimingsResponse;
import com.bouzidi.prayertimes.utils.TimingUtils;

import java.io.IOException;
import java.time.LocalDate;

import io.reactivex.rxjava3.core.Single;

public class PrayerHelper {

    public static Single<DayPrayer> getTimingsByCity(final LocalDate localDate, final String city, final String country,
                                                     final CalculationMethodEnum method, final Context context) {

        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                if (localDate == null || city == null || country == null) {
                    Log.e(PrayerHelper.class.getName(), "Cannot find timings with null attribute");
                    emitter.onError(new TimingsException("Cannot find timings with null attributes"));
                } else {
                    prayerTimings = prayerRegistry.getPrayerTimings(TimingUtils.formatDateForAdhanAPI(localDate), city, method);

                    if (prayerTimings != null) {
                        emitter.onSuccess(prayerTimings);
                    } else {
                        try {
                            AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
                            AladhanTodayTimingsResponse timingsByCity = aladhanAPIService.getTimingsByCity(localDate, city, country, method, context);
                            prayerRegistry.savePrayerTiming(TimingUtils.formatDateForAdhanAPI(localDate), city, country, method, timingsByCity);
                            prayerTimings = prayerRegistry.getPrayerTimings(TimingUtils.formatDateForAdhanAPI(localDate), city, method);

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

    public static Single<DayPrayer> fetchTimingsByCity(final LocalDate localDate, final String city, final String country,
                                                       final CalculationMethodEnum method, final Context context) {

        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                try {
                    AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
                    AladhanTodayTimingsResponse timingsByCity = aladhanAPIService.getTimingsByCity(localDate, city, country, method, context);
                    prayerRegistry.savePrayerTiming(TimingUtils.formatDateForAdhanAPI(localDate), city, country, method, timingsByCity);
                    prayerTimings = prayerRegistry.getPrayerTimings(TimingUtils.formatDateForAdhanAPI(localDate), city, method);

                    emitter.onSuccess(prayerTimings);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        });
    }
}
