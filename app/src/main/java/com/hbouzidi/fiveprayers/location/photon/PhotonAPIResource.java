package com.hbouzidi.fiveprayers.location.photon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PhotonAPIResource {

    @GET(".")
    Call<PhotonAPIResponse> search(@Query("q") String str,
                                   @Query("limit") int limit,
                                   @Query("lang") String lang);
}
