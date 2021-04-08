package com.hbouzidi.fiveprayers.location.photon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
public class PhotonAPIService {


    private final Retrofit retrofit;

    @Inject
    public PhotonAPIService(@Named("photon_api") Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public PhotonAPIResponse search(final String str, final int limit) throws IOException {
        PhotonAPIResource photonAPIResource = retrofit.create(PhotonAPIResource.class);

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
