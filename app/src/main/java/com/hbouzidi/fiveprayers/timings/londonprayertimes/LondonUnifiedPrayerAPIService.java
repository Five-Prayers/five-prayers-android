package com.hbouzidi.fiveprayers.timings.londonprayertimes;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.io.IOException;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class LondonUnifiedPrayerAPIService {

    private final static String API_KEY = BuildConfig.LONDON_UNIFIED_TIMINGS_API_KEY;
    private final static String DEFAULT_FORMAT = "json";
    private final static String TWENTY_FOUR_FORMAT = "true";

    private final Retrofit retrofit;

    @Inject
    public LondonUnifiedPrayerAPIService(@Named("lut_api") Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public LondonUnifiedTimingsResponse getLondonTimings() throws IOException {
        String localDateString = TimingUtils.formatDateForLUTAPI(LocalDate.now());

        LondonUnifiedPrayerAPIResource londonUnifiedPrayerAPIResource =
                retrofit.create(LondonUnifiedPrayerAPIResource.class);

        Call<LondonUnifiedTimingsResponse> call
                = londonUnifiedPrayerAPIResource
                .getLondonTimings(localDateString, TWENTY_FOUR_FORMAT, DEFAULT_FORMAT, API_KEY);

        return call.execute().body();
    }

    public LondonUnifiedCalendarResponse getLondonCalendar(final int month, final int year) throws IOException {

        LondonUnifiedPrayerAPIResource londonUnifiedPrayerAPIResource =
                retrofit.create(LondonUnifiedPrayerAPIResource.class);

        Call<LondonUnifiedCalendarResponse> call
                = londonUnifiedPrayerAPIResource
                .getMonthlyCalendar(month, year, TWENTY_FOUR_FORMAT, DEFAULT_FORMAT, API_KEY);

        return call.execute().body();
    }
}
