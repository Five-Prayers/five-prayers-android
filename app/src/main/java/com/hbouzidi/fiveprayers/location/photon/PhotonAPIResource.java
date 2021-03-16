package com.hbouzidi.fiveprayers.location.photon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public interface PhotonAPIResource {

    @GET(".")
    Call<PhotonAPIResponse> search(@Query("q") String str,
                                   @Query("limit") int limit,
                                   @Query("lang") String lang);
}
