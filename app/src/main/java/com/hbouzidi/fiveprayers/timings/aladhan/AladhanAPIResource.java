package com.hbouzidi.fiveprayers.timings.aladhan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public interface AladhanAPIResource {

    @GET("timingsByCity/{date_or_timestamp}")
    Call<AladhanTodayTimingsResponse> getTimingsByCity(
            @Path("date_or_timestamp") String date,
            @Query("city") String city,
            @Query("country") String country,
            @Query("method") int method,
            @Query("methodSettings") String methodSettings,
            @Query("latitudeAdjustmentMethod") int latitudeAdjustmentMethod,
            @Query("school") int school,
            @Query("midnightMode") int midnightMode,
            @Query("adjustment") int adjustment);

    @GET("timings/{date_or_timestamp}")
    Call<AladhanTodayTimingsResponse> getTimingsByLatLong(
            @Path("date_or_timestamp") String date,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method,
            @Query("methodSettings") String methodSettings,
            @Query("latitudeAdjustmentMethod") int latitudeAdjustmentMethod,
            @Query("school") int school,
            @Query("midnightMode") int midnightMode,
            @Query("adjustment") int adjustment,
            @Query("timezonestring") String timezonestring
    );

    @GET("calendarByCity")
    Call<AladhanCalendarResponse> getCalendarByCity(
            @Query("city") String city,
            @Query("country") String country,
            @Query("month") int month,
            @Query("year") int year,
            @Query("annual") boolean annual,
            @Query("method") int method,
            @Query("methodSettings") String methodSettings,
            @Query("latitudeAdjustmentMethod") int latitudeAdjustmentMethod,
            @Query("school") int school,
            @Query("midnightMode") int midnightMode,
            @Query("adjustment") int adjustment
    );

    @GET("calendar")
    Call<AladhanCalendarResponse> getCalendarByLatLong(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("month") int month,
            @Query("year") int year,
            @Query("annual") boolean annual,
            @Query("method") int method,
            @Query("methodSettings") String methodSettings,
            @Query("latitudeAdjustmentMethod") int latitudeAdjustmentMethod,
            @Query("school") int school,
            @Query("midnightMode") int midnightMode,
            @Query("adjustment") int adjustment,
            @Query("timezonestring") String timezonestring
    );
}
