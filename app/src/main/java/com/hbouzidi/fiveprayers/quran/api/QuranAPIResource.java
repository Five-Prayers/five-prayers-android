package com.hbouzidi.fiveprayers.quran.api;

import com.hbouzidi.fiveprayers.quran.dto.QuranResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuranAPIResource {

    @GET("quran/{edition}")
    Call<QuranResponse> getFullQuran(@Path("edition") String edition);
}
