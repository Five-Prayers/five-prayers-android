package com.hbouzidi.fiveprayers.openweathermap;

import android.util.Log;

import com.hbouzidi.fiveprayers.utils.LocaleHelper;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class OpenWeatherMapAPIService {

    protected String TAG = "OPEN_WEATHER_MAP_API_SERVICE";

    private final Retrofit retrofit;
    private final LocaleHelper localeHelper;

    @Inject
    public OpenWeatherMapAPIService(@Named("open_weather_map_api") Retrofit retrofit, LocaleHelper localeHelper) {
        this.retrofit = retrofit;
        this.localeHelper = localeHelper;
    }

    public Single<OpenWeatherMapResponse> getCurrentWeatherData(double latitude, double longitude, String appKey, TemperatureUnit weatherUnit) {
        OpenWeatherMapAPIResource openWeatherMapAPIResource = retrofit.create(OpenWeatherMapAPIResource.class);

        Call<OpenWeatherMapResponse> call
                = openWeatherMapAPIResource.getWeatherData(latitude, longitude, appKey, weatherUnit.toString(), "", localeHelper.getLocale().getLanguage());

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    Response<OpenWeatherMapResponse> response = call.execute();
                    OpenWeatherMapResponse openWeatherMapResponse = response.body();
                    if (openWeatherMapResponse != null) {
                        emitter.onSuccess(response.body());
                    } else {
                        Log.i(TAG, "Cannot get weather information");
                        emitter.onError(new Exception("Get null OpenWeatherMapResponse"));
                    }
                } catch (IOException e) {
                    Log.i(TAG, "Cannot get weather information", e);
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }
}