package com.hbouzidi.fiveprayers.shadows;

import com.hbouzidi.fiveprayers.location.osm.NominatimAPIService;

import org.robolectric.annotation.Implements;

@Implements(NominatimAPIService.class)
public class ShadowNominatimAPIService{

//    public ShadowNominatimAPIService() {
//        BASE_URL = RESTMockServer.getUrl();
//    }
//
//    public NominatimReverseGeocodeResponse getAddressFromLocation(double latitude, double longitude) throws IOException {
//        NominatimAPIResource nominatimAPIResource = provideRetrofit().create(NominatimAPIResource.class);
//
//        Call<NominatimReverseGeocodeResponse> call
//                = nominatimAPIResource.getReverseGeocode(latitude, longitude, 18, 1, "json");
//
//        Response<NominatimReverseGeocodeResponse> response = call.execute();
//
//        return response.body();
//    }
//
//    public Single<NominatimReverseGeocodeResponse> getAddressFromLatLong(double latitude, double longitude) {
//        NominatimAPIResource nominatimAPIResource = provideRetrofit().create(NominatimAPIResource.class);
//
//        return Single.create(emitter -> {
//            Thread thread = new Thread(() -> {
//                try {
//                    Call<NominatimReverseGeocodeResponse> call
//                            = nominatimAPIResource.getReverseGeocode(latitude, longitude, 18, 1, "json");
//
//                    emitter.onSuccess(call.execute().body());
//
//                } catch (IOException e) {
//                    Log.e(AddressHelper.class.getName(), "Unable connect to get address from Location", e);
//                    emitter.onError(new LocationException("Unable connect to get address from API", e));
//                }
//            });
//            thread.start();
//        });
//    }
}