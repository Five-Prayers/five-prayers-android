package com.hbouzidi.fiveprayers.openweathermap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public interface OpenWeatherMapAPIResource {

    @GET("weather")
    Call<OpenWeatherMapResponse> getWeatherData(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String appKey,
            @Query("units") String units,
            @Query("exclude") String excludePart,
            @Query("lang")  String language);
}
