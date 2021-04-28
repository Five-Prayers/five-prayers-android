package com.hbouzidi.fiveprayers.calendar;

import android.util.Log;

import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;
import com.hbouzidi.fiveprayers.timings.offline.OfflineTimingsService;

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
    private final OfflineTimingsService offlineTimingsService;
    private final PreferencesHelper preferencesHelper;

    @Inject
    public CalendarService(CalendarAPIService calendarAPIService,
                           OfflineTimingsService offlineTimingsService,
                           PreferencesHelper preferencesHelper) {
        this.calendarAPIService = calendarAPIService;
        this.offlineTimingsService = offlineTimingsService;
        this.preferencesHelper = preferencesHelper;
    }

    public Single<List<AladhanDate>> getHijriCalendar(final int month, final int year) {
        int hijriAdjustment = preferencesHelper.getHijriAdjustment();

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    CalendarAPIResponse hijriCalendar = calendarAPIService.getHijriCalendar(month, year, hijriAdjustment);

                    if (hijriCalendar != null) {
                        emitter.onSuccess(hijriCalendar.getData());
                    } else {
                        Log.i(CalendarService.class.getName(), "Offline calendar");
                        emitter.onSuccess(offlineTimingsService.getHijriCalendar(month, year, hijriAdjustment));
                    }

                } catch (IOException e) {
                    Log.i(CalendarService.class.getName(), "Offline calendar", e);
                    emitter.onSuccess(offlineTimingsService.getHijriCalendar(month, year, hijriAdjustment));
                }
            });
            thread.start();
        });
    }
}
