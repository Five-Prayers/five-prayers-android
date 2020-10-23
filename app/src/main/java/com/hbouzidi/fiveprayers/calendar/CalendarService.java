package com.hbouzidi.fiveprayers.calendar;

import android.content.Context;
import android.util.Log;

import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class CalendarService {

    private static CalendarService instance;

    private CalendarService() {
    }

    public static CalendarService getInstance() {
        if (instance == null) {
            instance = new CalendarService();
        }
        return instance;
    }

    public Single<List<AladhanDate>> getHijriCalendar(final int month,
                                                      final int year,
                                                      final Context context) {

        int hijriAdjustment = PreferencesHelper.getHijriAdjustment(context);

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    CalendarAPIService calendarAPIService = CalendarAPIService.getInstance(context);

                    emitter.onSuccess(calendarAPIService
                            .getHijriCalendar(month, year, hijriAdjustment).getData());

                } catch (IOException e) {
                    Log.e(CalendarService.class.getName(), "Cannot find from aladhanAPIService");
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }
}
