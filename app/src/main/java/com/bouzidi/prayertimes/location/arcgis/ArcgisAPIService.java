package com.bouzidi.prayertimes.location.arcgis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArcgisAPIService {

    private static final String BASE_URL = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/";
    private static ArcgisAPIService arcgisReverseGeocodeService;

    private ArcgisAPIService() {
    }

    public static ArcgisAPIService getInstance() {
        if (arcgisReverseGeocodeService == null) {
            arcgisReverseGeocodeService = new ArcgisAPIService();
        }
        return arcgisReverseGeocodeService;
    }

    public ArcgisReverseGeocodeResponse getAddressFromLocation(final double latitude, final double longitude) throws IOException {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("forStorage", "false")
                    .addQueryParameter("f", "pjson")
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ArcgisAPIResource arcgisAPIResource = retrofit.create(ArcgisAPIResource.class);

        String sb = longitude + "," + latitude;

        Call<ArcgisReverseGeocodeResponse> call
                = arcgisAPIResource.getReverseGeocode(sb);

        Response<ArcgisReverseGeocodeResponse> response = call.execute();

        return response.body();
    }
}
