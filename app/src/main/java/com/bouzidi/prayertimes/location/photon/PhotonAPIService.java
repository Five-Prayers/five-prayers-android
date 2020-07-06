package com.bouzidi.prayertimes.location.photon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotonAPIService {

    private static final String BASE_URL = "https://photon.komoot.de/api/";
    private static PhotonAPIService photonAPIService;

    private PhotonAPIService() {
    }

    public static PhotonAPIService getInstance() {
        if (photonAPIService == null) {
            photonAPIService = new PhotonAPIService();
        }
        return photonAPIService;
    }

    public PhotonAPIResponse search(final String str, final int limit) throws IOException {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PhotonAPIResource photonAPIResource = retrofit.create(PhotonAPIResource.class);

        Call<PhotonAPIResponse> call
                = photonAPIResource.search(str, limit);

        Response<PhotonAPIResponse> response = call.execute();

        return response.body();
    }
}
