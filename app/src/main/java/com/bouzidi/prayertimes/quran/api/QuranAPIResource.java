package com.bouzidi.prayertimes.quran.api;

import com.bouzidi.prayertimes.quran.dto.QuranResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuranAPIResource {

    @GET("quran/{edition}")
    Call<QuranResponse> getFullQuran(@Path("edition") String edition);
}
