package com.hbouzidi.fiveprayers.calendar;

import android.content.Context;
import android.util.Log;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;
import com.hbouzidi.fiveprayers.network.NetworkUtil;

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

public class CalendarAPIService extends BaseAPIService {

    private static final int CACHE_MAX_SIZE = 10 * 1024 * 1024; //10Mo
    private static final int CACHE_MAX_AGE = 1; // 1Day
    private static final int CACHE_MAX_STALE = 1; // 1Day

    private static CalendarAPIService calendarAPIService;

    private CalendarAPIService(Context context) {
        okHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(provideOfflineCacheInterceptor(context))
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .cache(provideCache(context))
                        .build();
        BASE_URL = "https://api.aladhan.com/v1/";
    }

    public static CalendarAPIService getInstance(Context context) {
        if (calendarAPIService == null) {
            calendarAPIService = new CalendarAPIService(context);
        }
        return calendarAPIService;
    }


    public CalendarAPIResponse getHijriCalendar(int month,
                                                int year,
                                                int adjustment) throws IOException {


        CalendarAPIResource calendarAPIResource = provideRetrofit().create(CalendarAPIResource.class);

        Call<CalendarAPIResponse> call
                = calendarAPIResource
                .getHijriCalendar(month, year, adjustment);

        return call.execute().body();
    }

    private static Cache provideCache(final Context context) {
        Cache cache = null;
        try {
            cache = new Cache(
                    new File(context.getApplicationContext().getCacheDir(), "http-cache"),
                    CACHE_MAX_SIZE
            );
        } catch (Exception e) {
            Log.e(CalendarAPIService.class.getName(), "Could not create Cache!");
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
