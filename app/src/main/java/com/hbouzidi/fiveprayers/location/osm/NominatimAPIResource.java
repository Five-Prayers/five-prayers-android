package com.hbouzidi.fiveprayers.location.osm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimAPIResource {

    @GET("reverse")
    Call<NominatimReverseGeocodeResponse> getReverseGeocode(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("zoom") int zoom,
            @Query("addressdetails") int addressdetails,
            @Query("format") String format);
}
