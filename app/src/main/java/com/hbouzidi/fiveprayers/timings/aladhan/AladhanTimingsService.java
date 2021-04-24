package com.hbouzidi.fiveprayers.timings.aladhan;

import android.location.Address;

import com.hbouzidi.fiveprayers.database.PrayerRegistry;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.AbstractTimingsService;
import com.hbouzidi.fiveprayers.timings.TimingsPreferences;

import java.io.IOException;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class AladhanTimingsService extends AbstractTimingsService {

    private final AladhanAPIService aladhanAPIService;
    protected String TAG = "AladhanTimingsService";

    @Inject
    public AladhanTimingsService(AladhanAPIService aladhanAPIService, PrayerRegistry prayerRegistry, PreferencesHelper preferencesHelper) {
        super(prayerRegistry, preferencesHelper);
        this.aladhanAPIService = aladhanAPIService;
    }

    protected void retrieveAndSaveTimings(LocalDate localDate, Address address) throws IOException {
        TimingsPreferences timingsPreferences = getTimingsPreferences();

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

        if (timingsByCity != null) {
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
    }

    protected void retrieveAndSaveCalendar(Address address, int month, int year) throws IOException {
        TimingsPreferences timingsPreferences = getTimingsPreferences();

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

        if (CalendarByCity != null) {
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
}
