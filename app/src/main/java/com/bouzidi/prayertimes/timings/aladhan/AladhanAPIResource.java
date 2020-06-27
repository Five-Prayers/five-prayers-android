package com.bouzidi.prayertimes.timings.aladhan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AladhanAPIResource {

    @GET("timingsByCity/{date_or_timestamp}")
    Call<AladhanTodayTimingsResponse> getTimingsByCity(@Path("date_or_timestamp") String date,
                                                       @Query("city") String city,
                                                       @Query("country") String country,
                                                       @Query("method") int method,
                                                       @Query("latitudeAdjustmentMethod") int latitudeAdjustmentMethod,
                                                       @Query("adjustment") int adjustment,
                                                       @Query("tune") String tune);

    @GET("timings/{date_or_timestamp}")
    Call<AladhanTodayTimingsResponse> getTimingsByLatLong(@Path("date_or_timestamp") String date,
                                                          @Query("latitude") String latitude,
                                                          @Query("longitude") Double longitude,
                                                          @Query("method") int method,
                                                          @Query("latitudeAdjustmentMethod") int latitudeAdjustmentMethod,
                                                          @Query("adjustment") int adjustment,
                                                          @Query("tune") String tune
    );

    @GET("calendarByCity")
    Call<AladhanCalendarResponse> getCalendarByCity(@Query("city") String city,
                                                    @Query("country") String country,
                                                    @Query("month") int month,
                                                    @Query("year") int year,
                                                    @Query("annual") boolean annual,
                                                    @Query("method") int method,
                                                    @Query("latitudeAdjustmentMethod") int latitudeAdjustmentMethod,
                                                    @Query("adjustment") int adjustment,
                                                    @Query("tune") String tune
    );
}
