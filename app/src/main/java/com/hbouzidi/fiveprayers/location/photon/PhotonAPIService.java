package com.hbouzidi.fiveprayers.location.photon;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;

public class PhotonAPIService extends BaseAPIService {

    private static PhotonAPIService photonAPIService;

    private PhotonAPIService() {
        okHttpClient = new OkHttpClient.Builder().build();
        BASE_URL = "https://photon.komoot.de/api/";
    }

    public static PhotonAPIService getInstance() {
        if (photonAPIService == null) {
            photonAPIService = new PhotonAPIService();
        }
        return photonAPIService;
    }

    public PhotonAPIResponse search(final String str, final int limit) throws IOException {
        PhotonAPIResource photonAPIResource = provideRetrofit().create(PhotonAPIResource.class);

        Call<PhotonAPIResponse> call
                = photonAPIResource.search(str, limit, getDefaultLanguage());

        Response<PhotonAPIResponse> response = call.execute();

        return response.body();
    }

    private String getDefaultLanguage() {
        List<String> supportedLanguages = new ArrayList<>(4);

        supportedLanguages.addAll(Arrays.asList("de", "en", "it", "fr"));
        String defaultLanguage = Locale.getDefault().getLanguage();

        if (supportedLanguages.contains(defaultLanguage)) {
            return defaultLanguage;
        }
        return Locale.ENGLISH.getLanguage();
    }
}
