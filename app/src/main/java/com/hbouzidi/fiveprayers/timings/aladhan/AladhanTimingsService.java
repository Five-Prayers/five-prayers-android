package com.hbouzidi.fiveprayers.timings.aladhan;

import android.content.Context;
import android.location.Address;

import com.hbouzidi.fiveprayers.database.PrayerRegistry;
import com.hbouzidi.fiveprayers.timings.AbstractTimingsService;
import com.hbouzidi.fiveprayers.timings.TimingsPreferences;

import java.io.IOException;
import java.time.LocalDate;

public class AladhanTimingsService extends AbstractTimingsService {

    protected String TAG = "AladhanTimingsService";
    private static AladhanTimingsService instance;

    private AladhanTimingsService() {
    }

    public static AladhanTimingsService getInstance() {
        if (instance == null) {
            instance = new AladhanTimingsService();
        }
        return instance;
    }

    protected void retrieveAndSaveTimings(LocalDate localDate, Address address, Context context) throws IOException {
        TimingsPreferences timingsPreferences = getTimingsPreferences(context);

        AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
        AladhanTodayTimingsResponse timingsByCity =
                aladhanAPIService.getTimingsByLatLong(
                        address.getLatitude(),
                        address.getLongitude(),
                        timingsPreferences.getMethod(),
                        timingsPreferences.getLatitudeAdjustmentMethod(),
                        timingsPreferences.getSchoolAdjustmentMethod(),
                        timingsPreferences.getMidnightModeAdjustmentMethod(),
                        timingsPreferences.getHijriAdjustment(),
                        timingsPreferences.getTune());

        PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);
        prayerRegistry.savePrayerTiming(localDate,
                address.getLocality(),
                address.getCountryName(),
                timingsPreferences.getMethod(),
                timingsPreferences.getLatitudeAdjustmentMethod(),
                timingsPreferences.getSchoolAdjustmentMethod(),
                timingsPreferences.getMidnightModeAdjustmentMethod(),
                timingsPreferences.getHijriAdjustment(),
                timingsPreferences.getTune(),
                timingsByCity.getData()
        );
    }

    protected void retrieveAndSaveCalendar(Address address, int month, int year, Context context) throws IOException {
        TimingsPreferences timingsPreferences = getTimingsPreferences(context);

        AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
        AladhanCalendarResponse CalendarByCity =
                aladhanAPIService.getCalendarByLatLong(
                        address.getLatitude(),
                        address.getLongitude(),
                        month, year,
                        timingsPreferences.getMethod(),
                        timingsPreferences.getLatitudeAdjustmentMethod(),
                        timingsPreferences.getSchoolAdjustmentMethod(),
                        timingsPreferences.getMidnightModeAdjustmentMethod(),
                        timingsPreferences.getHijriAdjustment(),
                        timingsPreferences.getTune());

        PrayerRegistry prayerRegistry = PrayerRegistry.getInstance(context);
        prayerRegistry.saveCalendar(
                address.getLocality(),
                address.getCountryName(),
                timingsPreferences.getMethod(),
                timingsPreferences.getLatitudeAdjustmentMethod(),
                timingsPreferences.getSchoolAdjustmentMethod(),
                timingsPreferences.getMidnightModeAdjustmentMethod(),
                timingsPreferences.getHijriAdjustment(),
                timingsPreferences.getTune(),
                CalendarByCity.getData());
    }
}
