package com.hbouzidi.fiveprayers.location.osm;

import android.util.Log;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;
import com.hbouzidi.fiveprayers.exceptions.LocationException;
import com.hbouzidi.fiveprayers.location.address.AddressHelper;

import java.io.IOException;

import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NominatimAPIService extends BaseAPIService {

    private static NominatimAPIService nominatimAPIService;

    private NominatimAPIService() {
        BASE_URL = "https://nominatim.openstreetmap.org/";
    }

    public static NominatimAPIService getInstance() {
        if (nominatimAPIService == null) {
            nominatimAPIService = new NominatimAPIService();
        }
        return nominatimAPIService;
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