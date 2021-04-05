package com.hbouzidi.fiveprayers.shadows;

import android.util.Log;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;
import com.hbouzidi.fiveprayers.exceptions.LocationException;
import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIResource;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIService;
import com.hbouzidi.fiveprayers.location.osm.NominatimReverseGeocodeResponse;

import org.robolectric.annotation.Implements;

import java.io.IOException;

import io.appflate.restmock.RESTMockServer;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;

@Implements(NominatimAPIService.class)
public class ShadowNominatimAPIService extends BaseAPIService {

    public ShadowNominatimAPIService() {
        BASE_URL = RESTMockServer.getUrl();
    }

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
}