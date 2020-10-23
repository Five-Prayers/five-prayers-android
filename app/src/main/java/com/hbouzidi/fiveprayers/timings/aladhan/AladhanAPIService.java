package com.hbouzidi.fiveprayers.timings.aladhan;

import android.content.Context;
import android.util.Log;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;
import com.hbouzidi.fiveprayers.network.NetworkUtil;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class AladhanAPIService extends BaseAPIService {

    private static final int CACHE_MAX_SIZE = 10 * 1024 * 1024; //10Mo
    private static final int CACHE_MAX_AGE = 1; // 1Day
    private static final int CACHE_MAX_STALE = 1; // 1Day

    private static AladhanAPIService aladhanAPIService;

    private AladhanAPIService(Context context) {
        okHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(provideOfflineCacheInterceptor(context))
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .cache(provideCache(context))
                        .build();
        BASE_URL = "https://api.aladhan.com/v1/";
    }

    public static AladhanAPIService getInstance(Context context) {
        if (aladhanAPIService == null) {
            aladhanAPIService = new AladhanAPIService(context);
        }
        return aladhanAPIService;
    }

    public AladhanTodayTimingsResponse getTimingsByLatLong(final String LocalDateString,
                                                           final double latitude,
                                                           final double longitude,
                                                           final CalculationMethodEnum method,
                                                           final LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                                           SchoolAdjustmentMethod schoolAdjustmentMethod,
                                                           MidnightModeAdjustmentMethod midnightModeAdjustmentMethod, final int adjustment,
                                                           final String tune
    ) throws IOException {

        AladhanAPIResource aladhanAPIResource = provideRetrofit().create(AladhanAPIResource.class);

        Call<AladhanTodayTimingsResponse> call
                = aladhanAPIResource
                .getTimingsByLatLong(LocalDateString, latitude, longitude, method.getMethodId(),
                        getMethodSettings(method),
                        latitudeAdjustmentMethod.getValue(),
                        schoolAdjustmentMethod.getValue(),
                        midnightModeAdjustmentMethod.getValue(),
                        adjustment, tune);

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
                        tune);

        return call.execute().body();
    }

    @NotNull
    private String getMethodSettings(CalculationMethodEnum method) {
        return method.getFajrAngle() + "," + method.getMaghribAngle() + "," + method.getIchaAngle();
    }

    private static Cache provideCache(final Context context) {
        Cache cache = null;
        try {
            cache = new Cache(
                    new File(context.getApplicationContext().getCacheDir(), "http-cache"),
                    CACHE_MAX_SIZE
            );
        } catch (Exception e) {
            Log.e(AladhanAPIService.class.getName(), "Could not create Cache!");
        }
        return cache;
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            // re-write response header to force use of cache
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(CACHE_MAX_AGE, TimeUnit.DAYS)
                    .build();

            return response.newBuilder()
                    .header("Cache-Control", cacheControl.toString())
                    .build();
        };
    }

    private static Interceptor provideOfflineCacheInterceptor(final Context context) {
        return chain -> {
            Request request = chain.request();

            if (!NetworkUtil.isNetworkAvailable(context)) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(CACHE_MAX_STALE, TimeUnit.DAYS)
                        .onlyIfCached()
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }
}
