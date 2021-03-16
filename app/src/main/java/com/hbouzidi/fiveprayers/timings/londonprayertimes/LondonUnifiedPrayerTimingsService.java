package com.hbouzidi.fiveprayers.timings.londonprayertimes;

import android.content.Context;
import android.location.Address;

import com.hbouzidi.fiveprayers.database.PrayerRegistry;
import com.hbouzidi.fiveprayers.timings.AbstractTimingsService;
import com.hbouzidi.fiveprayers.timings.TimingsPreferences;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanAPIService;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanCalendarResponse;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanData;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimings;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTodayTimingsResponse;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class LondonUnifiedPrayerTimingsService extends AbstractTimingsService {

    protected String TAG = "LondonUnifiedPrayerTimingsService";

    private static LondonUnifiedPrayerTimingsService instance;

    private LondonUnifiedPrayerTimingsService() {
    }

    public static LondonUnifiedPrayerTimingsService getInstance() {
        if (instance == null) {
            instance = new LondonUnifiedPrayerTimingsService();
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

        LondonUnifiedPrayerAPIService londonUnifiedPrayerAPIService = LondonUnifiedPrayerAPIService.getInstance();
        LondonUnifiedTimingsResponse londonTimings = londonUnifiedPrayerAPIService.getLondonTimings();

        if (timingsByCity != null && londonTimings != null) {
            AladhanData aladhanData = createTimingsData(timingsPreferences.getSchoolAdjustmentMethod(), timingsByCity.getData(), londonTimings);

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
                    aladhanData
            );
        }
    }

    protected void retrieveAndSaveCalendar(Address address, int month, int year, Context context) throws IOException {
        List<AladhanData> aladhanCalendarData = new ArrayList<>();

        TimingsPreferences timingsPreferences = getTimingsPreferences(context);

        AladhanAPIService aladhanAPIService = AladhanAPIService.getInstance();
        AladhanCalendarResponse calendarByCity =
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

        LondonUnifiedPrayerAPIService londonUnifiedPrayerAPIService = LondonUnifiedPrayerAPIService.getInstance();
        LondonUnifiedCalendarResponse londonCalendar = londonUnifiedPrayerAPIService.getLondonCalendar(month, year);

        if (calendarByCity != null && londonCalendar != null) {

            for (AladhanData aladhanData : calendarByCity.getData()) {
                String londonDate = TimingUtils.ConvertAlAdhanFormatToLUT(aladhanData.getDate().getGregorian().getDate());
                LondonUnifiedTimingsResponse londonUnifiedTimingsResponse = londonCalendar.getTimes().get(londonDate);

                if (londonUnifiedTimingsResponse != null) {
                    aladhanCalendarData.add(createTimingsData(timingsPreferences.getSchoolAdjustmentMethod(), aladhanData, londonUnifiedTimingsResponse));
                }
            }

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
                    aladhanCalendarData);
        }
    }

    private AladhanData createTimingsData(SchoolAdjustmentMethod schoolAdjustmentMethod, AladhanData data, LondonUnifiedTimingsResponse londonTimings) {
        AladhanData aladhanData = new AladhanData();
        aladhanData.setDate(data.getDate());
        aladhanData.setMeta(data.getMeta());

        AladhanTimings aladhanTimings = new AladhanTimings();
        aladhanTimings.setFajr(londonTimings.getFajr());
        aladhanTimings.setDhuhr(londonTimings.getDhuhr());
        aladhanTimings.setAsr(SchoolAdjustmentMethod.getDefault().equals(schoolAdjustmentMethod) ? londonTimings.getAsr() : londonTimings.getAsrTwo());
        aladhanTimings.setMaghrib(londonTimings.getMagrib());
        aladhanTimings.setIsha(londonTimings.getIsha());
        aladhanTimings.setImsak(data.getTimings().getImsak());
        aladhanTimings.setMidnight(data.getTimings().getMidnight());
        aladhanTimings.setSunrise(data.getTimings().getSunrise());
        aladhanTimings.setSunset(data.getTimings().getSunset());

        aladhanData.setTimings(aladhanTimings);
        return aladhanData;
    }
}
