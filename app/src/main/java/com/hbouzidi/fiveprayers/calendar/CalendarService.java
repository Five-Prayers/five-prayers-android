package com.hbouzidi.fiveprayers.calendar;

import android.content.Context;
import android.util.Log;

import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class CalendarService {

    private final CalendarAPIService calendarAPIService;

    @Inject
    public CalendarService(CalendarAPIService calendarAPIService) {
        this.calendarAPIService = calendarAPIService;
    }

    public Single<List<AladhanDate>> getHijriCalendar(final int month,
                                                      final int year,
                                                      final Context context) {

        int hijriAdjustment = PreferencesHelper.getHijriAdjustment(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    CalendarAPIResponse hijriCalendar = calendarAPIService.getHijriCalendar(month, year, hijriAdjustment);

                    if (hijriCalendar != null) {
                        emitter.onSuccess(hijriCalendar.getData());
                    } else {
                        emitter.onError(new Exception("Cannot get Hijri Calendar. Response was null"));
                        Log.e(CalendarService.class.getName(), "Cannot get Hijri Calendar. Response was null");
                    }

                } catch (IOException e) {
                    Log.e(CalendarService.class.getName(), "Cannot get Hijri Calendar from API");
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }
}
