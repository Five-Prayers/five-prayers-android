package com.hbouzidi.fiveprayers.location.osm;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class NominatimAPIService {

    private final Retrofit retrofit;

    @Inject
    public NominatimAPIService(@Named("nominatim_api") Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public NominatimReverseGeocodeResponse getAddressFromLocation(double latitude, double longitude) throws IOException {
        NominatimAPIResource nominatimAPIResource = retrofit.create(NominatimAPIResource.class);

        Call<NominatimReverseGeocodeResponse> call
                = nominatimAPIResource.getReverseGeocode(latitude, longitude, 18, 1, "json");

        Response<NominatimReverseGeocodeResponse> response = call.execute();

        return response.body();
    }
}