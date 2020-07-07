package com.bouzidi.prayertimes.timings.aladhan;

import android.content.Context;
import android.util.Log;

import com.bouzidi.prayertimes.network.NetworkUtil;
import com.bouzidi.prayertimes.timings.calculations.CalculationMethodEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AladhanAPIService {

    private static final String BASE_URL = "https://api.aladhan.com/v1/";
    private static final int CACHE_MAX_SIZE = 10 * 1024 * 1024; //10Mo
    private static final int CACHE_MAX_AGE = 1; // 1Day
    private static final int CACHE_MAX_STALE = 1; // 1Day
    private static AladhanAPIService aladhanAPIService;

    private AladhanAPIService() {
    }

    public static AladhanAPIService getInstance() {
        if (aladhanAPIService == null) {
            aladhanAPIService = new AladhanAPIService();
        }
        return aladhanAPIService;
    }

    public AladhanTodayTimingsResponse getTimingsByCity(final String LocalDateString, final String city,
                                                        final String country,
                                                        final CalculationMethodEnum method,
                                                        final int latitudeAdjustmentMethod,
                                                        final int adjustment,
                                                        final String tune,
                                                        final Context context) throws IOException {

        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(provideOfflineCacheInterceptor(context))
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .cache(provideCache(context));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AladhanAPIResource aladhanAPIResource = retrofit.create(AladhanAPIResource.class);

        Call<AladhanTodayTimingsResponse> call
                = aladhanAPIResource
                .getTimingsByCity(LocalDateString, city, country, method.getMethodId(), getMethodSettings(method), latitudeAdjustmentMethod, adjustment, tune);

        return call.execute().body();
    }

    public AladhanCalendarResponse getCalendarByCity(final String city, final String country,
                                                     final int month, final int year,
                                                     final CalculationMethodEnum method,
                                                     final int latitudeAdjustmentMethod,
                                                     final int adjustment,
                                                     final String tune,
                                                     final Context context) throws IOException {

        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(provideOfflineCacheInterceptor(context))
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .cache(provideCache(context));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AladhanAPIResource aladhanAPIResource = retrofit.create(AladhanAPIResource.class);

        Call<AladhanCalendarResponse> call
                = aladhanAPIResource
                .getCalendarByCity(city, country, month, year, false, method.getMethodId(), getMethodSettings(method), latitudeAdjustmentMethod, adjustment, tune);

        return call.execute().body();
    }

    public AladhanGToHCalendarResponse getHijriCalendar(int month,
                                                        int year,
                                                        int adjustment,
                                                        Context context) throws IOException {

        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(provideOfflineCacheInterceptor(context))
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .cache(provideCache(context));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AladhanAPIResource aladhanAPIResource = retrofit.create(AladhanAPIResource.class);

        Call<AladhanGToHCalendarResponse> call
                = aladhanAPIResource
                .getHijriCalendar(month, year, adjustment);

        return call.execute().body();
    }

    private static Cache provideCache(final Context context) {
        Cache cache = null;
        try {
            cache = new Cache(new File(context.getApplicationContext().getCacheDir(), "http-cache"),
                    CACHE_MAX_SIZE); // 10 MB
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

    @NotNull
    private String getMethodSettings(CalculationMethodEnum method) {
        return method.getFajrAngle() + "," + method.getMaghribAngle() + "," + method.getIchaAngle();
    }
}
