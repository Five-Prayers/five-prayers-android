package com.hbouzidi.fiveprayers.timings;

import android.content.Context;
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

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public abstract class AbstractTimingsService implements TimingsService {

    protected final PrayerRegistry prayerRegistry;
    protected String TAG = "AbstractTimingsService";

    public AbstractTimingsService(PrayerRegistry prayerRegistry) {
        this.prayerRegistry = prayerRegistry;
    }

    protected abstract void retrieveAndSaveTimings(LocalDate localDate, Address address, Context context) throws IOException;

    protected abstract void retrieveAndSaveCalendar(Address address, int month, int year, Context context) throws IOException;

    public Single<DayPrayer> getTimingsByCity(final LocalDate localDate,
                                              final Address address,
                                              final Context context) {

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                DayPrayer prayerTimings;

                if (address == null) {
                    Log.e(TAG, "Cannot find timings, address must not be null");
                    emitter.onError(new TimingsException("Cannot find timings, address must not be null"));
                } else {
                    prayerTimings = getSavedPrayerTimings(localDate, address, context);

                    if (prayerTimings != null) {
                        emitter.onSuccess(prayerTimings);
                    } else {
                        try {
                            retrieveAndSaveTimings(localDate, address, context);
                            prayerTimings = getSavedPrayerTimings(localDate, address, context);

                            emitter.onSuccess(prayerTimings);

                        } catch (IOException e) {
                            Log.e(TAG, "Cannot retrieve timing from API", e);
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

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                List<DayPrayer> prayerCalendar;

                if (address == null) {
                    Log.e(TAG, "Cannot find calendar, address must not be null");
                    emitter.onError(new TimingsException("Cannot find calendar, address must not be null"));
                } else {
                    prayerCalendar = getSavedPrayerCalendar(address, month, year, context);

                    if (prayerCalendar.size() == YearMonth.of(year, month).lengthOfMonth()) {
                        emitter.onSuccess(prayerCalendar);
                    } else {
                        try {
                            retrieveAndSaveCalendar(address, month, year, context);
                            prayerCalendar = getSavedPrayerCalendar(address, month, year, context);

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

    protected DayPrayer getSavedPrayerTimings(LocalDate localDate, Address address, Context context) {
        TimingsPreferences timingsPreferences = getTimingsPreferences(context);

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

    protected List<DayPrayer> getSavedPrayerCalendar(Address address, int month, int year, Context context) {
        TimingsPreferences timingsPreferences = getTimingsPreferences(context);

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

    protected TimingsPreferences getTimingsPreferences(Context context) {
        CalculationMethodEnum method = PreferencesHelper.getCalculationMethod(context);
        String tune = PreferencesHelper.getTune(context);
        LatitudeAdjustmentMethod latitudeAdjustmentMethod = PreferencesHelper.getLatitudeAdjustmentMethod(context);
        SchoolAdjustmentMethod schoolAdjustmentMethod = PreferencesHelper.getSchoolAdjustmentMethod(context);
        MidnightModeAdjustmentMethod midnightModeAdjustmentMethod = PreferencesHelper.getMidnightModeAdjustmentMethod(context);
        int hijriAdjustment = PreferencesHelper.getHijriAdjustment(context);

        return new TimingsPreferences(method, tune, latitudeAdjustmentMethod, schoolAdjustmentMethod,
                midnightModeAdjustmentMethod, hijriAdjustment
        );
    }
}
