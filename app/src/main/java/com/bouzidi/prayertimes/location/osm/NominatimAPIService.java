package com.bouzidi.prayertimes.location.osm;

import android.util.Log;

import com.bouzidi.prayertimes.exceptions.LocationException;
import com.bouzidi.prayertimes.location.address.AddressHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import io.reactivex.rxjava3.core.Single;
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

    public Single<NominatimReverseGeocodeResponse> getAddressFromLatLong(double latitude, double longitude) {
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


        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    Call<NominatimReverseGeocodeResponse> call
                            = nominatimAPIResource.getReverseGeocode(latitude, longitude, 18, 1, "json");

                    emitter.onSuccess(call.execute().body());

                } catch (IOException e) {
                    Log.e(AddressHelper.class.getName(), "Unable connect to get address from Location", e);
                    emitter.onError(new LocationException("Unable connect to get address from API", e));
                }
            });
            thread.start();
        });
    }
}
