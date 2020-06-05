package com.bouzidi.prayertimes.location.arcgis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArcgisAPIResource {

    @GET("reverseGeocode")
    Call<ArcgisReverseGeocodeResponse> getReverseGeocode(@Query("location") String location);
}
