package com.bouzidi.prayertimes.timings;

import android.content.Context;

import com.bouzidi.prayertimes.database.PrayerRegistry;
import com.bouzidi.prayertimes.timings.aladhan.AladhanAPIService;
import com.bouzidi.prayertimes.timings.aladhan.AladhanTodayTimingsResponse;
import com.bouzidi.prayertimes.utils.TimingUtils;

import java.io.IOException;
import java.util.Date;

import io.reactivex.rxjava3.core.Single;

public class PrayerHelper {

    public static Single<DayPrayer> getTimingsByCity(final Date date, final String city, final String country,
                                                     final CalculationMethodEnum method, final Context context) {

        if (date == null || city == null || country == null) { //FIXME Handle null errors !!
            return null;
        }
        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                prayerTimings = prayerRegistry.getPrayerTimings(TimingUtils.formatDate(date), city, method);

                if (prayerTimings != null) {
                    emitter.onSuccess(prayerTimings);
                } else {
                    try {
                        AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
                        AladhanTodayTimingsResponse timingsByCity = aladhanAPIService.getTimingsByCity(date, city, country, method, context);
                        prayerRegistry.savePrayerTiming(TimingUtils.formatDate(date), city, country, method, timingsByCity);
                        prayerTimings = prayerRegistry.getPrayerTimings(TimingUtils.formatDate(date), city, method);

                        emitter.onSuccess(prayerTimings);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        });
    }

    public static Single<DayPrayer> fetchTimingsByCity(final Date date, final String city, final String country,
                                                       final CalculationMethodEnum method, final Context context) {

        final PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                try {
                    AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
                    AladhanTodayTimingsResponse timingsByCity = aladhanAPIService.getTimingsByCity(date, city, country, method, context);
                    prayerRegistry.savePrayerTiming(TimingUtils.formatDate(date), city, country, method, timingsByCity);
                    prayerTimings = prayerRegistry.getPrayerTimings(TimingUtils.formatDate(date), city, method);

                    emitter.onSuccess(prayerTimings);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        });
    }
}
