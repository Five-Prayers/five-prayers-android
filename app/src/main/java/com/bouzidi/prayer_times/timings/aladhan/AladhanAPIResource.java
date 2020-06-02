package com.bouzidi.prayer_times.timings.aladhan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladhanAPIResource {

    @GET("timingsByCity")
    Call<AladhanTodayTimingsResponse> getTimingsByCity(@Query("date_or_timestamp") String date,
                                                       @Query("city") String city,
                                                       @Query("country") String country,
                                                       @Query("method") int method);

    @GET("timings")
    Call<AladhanTodayTimingsResponse> getTimingsByLatLong(@Query("latitude") String latitude,
                                                          @Query("longitude") Double longitude,
                                                          @Query("date_or_timestamp") String date,
                                                          @Query("method") int method);
}
