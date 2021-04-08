package com.hbouzidi.fiveprayers.shadows;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanAPIResource;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanAPIService;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanCalendarResponse;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTodayTimingsResponse;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;

import org.robolectric.annotation.Implements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import io.appflate.restmock.RESTMockServer;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Implements(AladhanAPIService.class)
public class ShadowAladhanAPIService {

    public ShadowAladhanAPIService() {
    }

    public AladhanTodayTimingsResponse getTimingsByLatLong(final double latitude,
                                                           final double longitude,
                                                           final CalculationMethodEnum method,
                                                           final LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                                           SchoolAdjustmentMethod schoolAdjustmentMethod,
                                                           MidnightModeAdjustmentMethod midnightModeAdjustmentMethod, final int adjustment,
                                                           final String tune
    ) throws IOException {

        long epochSecond = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        AladhanAPIResource aladhanAPIResource = provideRetrofit().create(AladhanAPIResource.class);

        Call<AladhanTodayTimingsResponse> call
                = aladhanAPIResource
                .getTimingsByLatLong(String.valueOf(epochSecond), latitude, longitude, method.getMethodId(),
                        getMethodSettings(method),
                        latitudeAdjustmentMethod.getValue(),
                        schoolAdjustmentMethod.getValue(),
                        midnightModeAdjustmentMethod.getValue(),
                        adjustment, tune, ZoneId.systemDefault().getId());

        return call.execute().body();
    }

    public AladhanCalendarResponse getCalendarByLatLong(
            final double latitude,
            final double longitude,
            final int month, final int year,
            final CalculationMethodEnum method,
            final LatitudeAdjustmentMethod latitudeAdjustmentMethod,
            SchoolAdjustmentMethod schoolAdjustmentMethod,
            MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
            final int adjustment,
            final String tune) throws IOException {

        AladhanAPIResource aladhanAPIResource = provideRetrofit().create(AladhanAPIResource.class);

        Call<AladhanCalendarResponse> call
                = aladhanAPIResource
                .getCalendarByLatLong(latitude, longitude, month, year, false, method.getMethodId(),
                        getMethodSettings(method),
                        latitudeAdjustmentMethod.getValue(),
                        schoolAdjustmentMethod.getValue(),
                        midnightModeAdjustmentMethod.getValue(),
                        adjustment,
                        tune,
                        ZoneId.systemDefault().getId()
                );

        return call.execute().body();
    }

    @NonNull
    private String getMethodSettings(CalculationMethodEnum method) {
        return method.getFajrAngle() + "," + method.getMaghribAngle() + "," + method.getIchaAngle();
    }

    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .baseUrl(RESTMockServer.getUrl())
                .client(provideNonCachedOkHttpClient())
                .build();
    }

    Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    OkHttpClient provideNonCachedOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        return builder
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
