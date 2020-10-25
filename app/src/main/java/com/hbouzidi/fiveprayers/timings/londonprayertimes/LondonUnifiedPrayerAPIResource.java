package com.hbouzidi.fiveprayers.timings.londonprayertimes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LondonUnifiedPrayerAPIResource {

    @GET("times")
    Call<LondonUnifiedTimingsResponse> getLondonTimings(
            @Query("date") String date,
            @Query("24hours") String twentyFourFormat,
            @Query("format") String format,
            @Query("key") String key
    );

    @GET("times")
    Call<LondonUnifiedCalendarResponse> getMonthlyCalendar(
            @Query("month") int month,
            @Query("year") int year,
            @Query("24hours") String twentyFourFormat,
            @Query("format") String format,
            @Query("key") String key
    );
}
