package com.hbouzidi.fiveprayers.timings.londonprayertimes;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.common.api.BaseAPIService;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.io.IOException;
import java.time.LocalDate;

import retrofit2.Call;

public class LondonUnifiedPrayerAPIService extends BaseAPIService {

    private static LondonUnifiedPrayerAPIService londonUnifiedPrayerAPIService;

    private final static String API_KEY = BuildConfig.LONDON_UNIFIED_TIMINGS_API_KEY;
    private final static String DEFAULT_FORMAT = "json";
    private final static String TWENTY_FOUR_FORMAT = "true";

    private LondonUnifiedPrayerAPIService() {
        BASE_URL = "https://www.londonprayertimes.com/api/";
    }

    public static LondonUnifiedPrayerAPIService getInstance() {
        if (londonUnifiedPrayerAPIService == null) {
            londonUnifiedPrayerAPIService = new LondonUnifiedPrayerAPIService();
        }
        return londonUnifiedPrayerAPIService;
    }

    public LondonUnifiedTimingsResponse getLondonTimings(final LocalDate localDate) throws IOException {
        String localDateString = TimingUtils.formatDateForLUTAPI(localDate);

        LondonUnifiedPrayerAPIResource londonUnifiedPrayerAPIResource =
                provideRetrofit().create(LondonUnifiedPrayerAPIResource.class);

        Call<LondonUnifiedTimingsResponse> call
                = londonUnifiedPrayerAPIResource
                .getLondonTimings(localDateString, TWENTY_FOUR_FORMAT, DEFAULT_FORMAT, API_KEY);

        return call.execute().body();
    }

    public LondonUnifiedCalendarResponse getLondonCalendar(final int month, final int year) throws IOException {

        LondonUnifiedPrayerAPIResource londonUnifiedPrayerAPIResource =
                provideRetrofit().create(LondonUnifiedPrayerAPIResource.class);

        Call<LondonUnifiedCalendarResponse> call
                = londonUnifiedPrayerAPIResource
                .getMonthlyCalendar(month, year, TWENTY_FOUR_FORMAT, DEFAULT_FORMAT, API_KEY);

        return call.execute().body();
    }
}
