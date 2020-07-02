package com.bouzidi.prayertimes.location.osm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NominatimAPIService {

    private static final String BASE_URL = "https://nominatim.openstreetmap.org/";
    private static NominatimAPIService nominatimAPIService;

    private NominatimAPIService() {
    }

    public static NominatimAPIService getInstance() {
        if (nominatimAPIService == null) {
            nominatimAPIService = new NominatimAPIService();
        }
        return nominatimAPIService;
    }

    public NominatimReverseGeocodeResponse getAddressFromLocation(double latitude, double longitude) throws IOException {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        NominatimAPIResource nominatimAPIResource = retrofit.create(NominatimAPIResource.class);

        Call<NominatimReverseGeocodeResponse> call
                = nominatimAPIResource.getReverseGeocode(latitude, longitude, 18, 1, "json");

        Response<NominatimReverseGeocodeResponse> response = call.execute();

        return response.body();
    }
}
