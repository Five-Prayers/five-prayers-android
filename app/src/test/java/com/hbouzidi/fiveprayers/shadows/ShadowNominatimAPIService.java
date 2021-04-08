package com.hbouzidi.fiveprayers.shadows;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbouzidi.fiveprayers.exceptions.LocationException;
import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIResource;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIService;
import com.hbouzidi.fiveprayers.location.osm.NominatimReverseGeocodeResponse;

import org.robolectric.annotation.Implements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.appflate.restmock.RESTMockServer;
import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Implements(NominatimAPIService.class)
public class ShadowNominatimAPIService {

    public NominatimReverseGeocodeResponse getAddressFromLocation(double latitude, double longitude) throws IOException {
        NominatimAPIResource nominatimAPIResource = provideRetrofit().create(NominatimAPIResource.class);

        Call<NominatimReverseGeocodeResponse> call
                = nominatimAPIResource.getReverseGeocode(latitude, longitude, 18, 1, "json");

        Response<NominatimReverseGeocodeResponse> response = call.execute();

        return response.body();
    }

    public Single<NominatimReverseGeocodeResponse> getAddressFromLatLong(double latitude, double longitude) {
        NominatimAPIResource nominatimAPIResource = provideRetrofit().create(NominatimAPIResource.class);

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