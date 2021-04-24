package com.hbouzidi.fiveprayers.timings;

import android.location.Address;
import android.util.Log;

import com.hbouzidi.fiveprayers.database.PrayerRegistry;
import com.hbouzidi.fiveprayers.exceptions.TimingsException;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public abstract class AbstractTimingsService implements TimingsService {

    protected final PrayerRegistry prayerRegistry;
    protected final PreferencesHelper preferencesHelper;
    protected String TAG = "AbstractTimingsService";

    public AbstractTimingsService(PrayerRegistry prayerRegistry, PreferencesHelper preferencesHelper) {
        this.prayerRegistry = prayerRegistry;
        this.preferencesHelper = preferencesHelper;
    }

    protected abstract void retrieveAndSaveTimings(LocalDate localDate, Address address) throws IOException;

    protected abstract void retrieveAndSaveCalendar(Address address, int month, int year) throws IOException;

    public Single<DayPrayer> getTimingsByCity(final LocalDate localDate, final Address address) {

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                if (address == null) {
                    Log.e(TAG, "Cannot find timings, address must not be null");
                    emitter.onError(new TimingsException("Cannot find timings, address must not be null"));
                } else {
                    prayerTimings = getSavedPrayerTimings(localDate, address);

                    if (prayerTimings != null) {
                        emitter.onSuccess(prayerTimings);
                    } else {
                        try {
                            retrieveAndSaveTimings(localDate, address);
                            prayerTimings = getSavedPrayerTimings(localDate, address);

                            if (prayerTimings != null) {
                                emitter.onSuccess(prayerTimings);
                            } else {
                                emitter.onError(new Exception("Cannot retrieve timings from API"));
                            }

                        } catch (IOException e) {
                            Log.e(TAG, "Cannot retrieve timings from API", e);
                            emitter.onError(e);
                        }
                    }
                }
            });
            thread.start();
        });
    }

    public Single<List<DayPrayer>> getCalendarByCity(final Address address, int month, int year) {

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                List<DayPrayer> prayerCalendar;

                if (address == null) {
                    Log.e(TAG, "Cannot find calendar, address must not be null");
                    emitter.onError(new TimingsException("Cannot find calendar, address must not be null"));
                } else {
                    prayerCalendar = getSavedPrayerCalendar(address, month, year);

                    if (prayerCalendar.size() == YearMonth.of(year, month).lengthOfMonth()) {
                        emitter.onSuccess(prayerCalendar);
                    } else {
                        try {
                            retrieveAndSaveCalendar(address, month, year);
                            prayerCalendar = getSavedPrayerCalendar(address, month, year);

                            emitter.onSuccess(prayerCalendar);
                        } catch (IOException e) {
                            Log.e(TAG, "Cannot find calendar from API");
                            emitter.onError(e);
                        }
                    }
                }
            });
            thread.start();
        });
    }

    protected DayPrayer getSavedPrayerTimings(LocalDate localDate, Address address) {
        TimingsPreferences timingsPreferences = getTimingsPreferences();

        if (address.getLocality() != null && address.getCountryName() != null) {
            return prayerRegistry.getPrayerTimings(
                    localDate,
                    address.getLocality(),
                    address.getCountryName(),
                    timingsPreferences.getMethod(),
                    timingsPreferences.getLatitudeAdjustmentMethod(),
                    timingsPreferences.getSchoolAdjustmentMethod(),
                    timingsPreferences.getMidnightModeAdjustmentMethod(),
                    timingsPreferences.getHijriAdjustment(),
                    timingsPreferences.getTune()
            );
        }

        return null;
    }

    protected List<DayPrayer> getSavedPrayerCalendar(Address address, int month, int year) {
        TimingsPreferences timingsPreferences = getTimingsPreferences();

        return prayerRegistry.getPrayerCalendar(
                address.getLocality(),
                address.getCountryName(),
                month, year,
                timingsPreferences.getMethod(),
                timingsPreferences.getLatitudeAdjustmentMethod(),
                timingsPreferences.getSchoolAdjustmentMethod(),
                timingsPreferences.getMidnightModeAdjustmentMethod(),
                timingsPreferences.getHijriAdjustment(),
                timingsPreferences.getTune()
        );
    }

    protected TimingsPreferences getTimingsPreferences() {
        CalculationMethodEnum method = preferencesHelper.getCalculationMethod();
        String tune = preferencesHelper.getTune();
        LatitudeAdjustmentMethod latitudeAdjustmentMethod = preferencesHelper.getLatitudeAdjustmentMethod();
        SchoolAdjustmentMethod schoolAdjustmentMethod = preferencesHelper.getSchoolAdjustmentMethod();
        MidnightModeAdjustmentMethod midnightModeAdjustmentMethod = preferencesHelper.getMidnightModeAdjustmentMethod();
        int hijriAdjustment = preferencesHelper.getHijriAdjustment();

        return new TimingsPreferences(method, tune, latitudeAdjustmentMethod, schoolAdjustmentMethod,
                midnightModeAdjustmentMethod, hijriAdjustment
        );
    }
}
