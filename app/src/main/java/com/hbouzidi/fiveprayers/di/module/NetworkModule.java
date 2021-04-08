package com.hbouzidi.fiveprayers.di.module;

import android.app.Application;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbouzidi.fiveprayers.common.api.TLSSocketFactoryCompat;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hbouzidi.fiveprayers.BuildConfig.DEBUG;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Module
public class NetworkModule {

    private final static String NOMINATIM_API_BASE_URL = "https://nominatim.openstreetmap.org/";
    private final static String LUT_API_BASE_URL = "https://www.londonprayertimes.com/api/";

    public NetworkModule() {
    }

    @Named("nominatim_api")
    @Provides
    @Singleton
    Retrofit provideNominatimApiRetrofit(Gson gson, @Named("nonCached") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(NOMINATIM_API_BASE_URL)
                .client(okHttpClient)
                .build();
    }

    @Named("adhan_api")
    @Provides
    @Singleton
    Retrofit provideAdhanApiRetrofit(Gson gson, @Named("nonCached") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(getAdhanAPIBaseUrl())
                .client(okHttpClient)
                .build();
    }

    @Named("lut_api")
    @Provides
    @Singleton
    Retrofit provideLUTApiRetrofit(Gson gson, @Named("nonCached") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(LUT_API_BASE_URL)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Named("nonCached")
    @Provides
    @Singleton
    OkHttpClient provideNonCachedOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            enableModernTLS(builder);
        }

        return builder
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Named("cached")
    @Provides
    @Singleton
    OkHttpClient provideCachedOkHttpClient(Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            enableModernTLS(builder);
        }

        return builder
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(cache)
                .build();
    }


    private String getAdhanAPIBaseUrl() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return "http://api.aladhan.com/v1/";
        }
        return "https://api.aladhan.com/v1/";
    }

    /**
     * Enable TLS 1.2 and 1.1 on Android Kitkat. This function is mostly taken
     * from the documentation of OkHttpClient.Builder.sslSocketFactory(_,_).
     * <p>
     * If there is an error, the function will safely fall back to doing nothing
     * and printing the error to the console.
     * </p>
     *
     * @param builder The HTTPClient Builder on which TLS is enabled on (will be modified in-place)
     */
    private static void enableModernTLS(final OkHttpClient.Builder builder) {
        try {
            // get the default TrustManager
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            final X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // insert our own TLSSocketFactory
            final SSLSocketFactory sslSocketFactory = TLSSocketFactoryCompat.getInstance();

            builder.sslSocketFactory(sslSocketFactory, trustManager);

            // This will try to enable all modern CipherSuites(+2 more)
            // that are supported on the device.
            // Necessary because some servers (e.g. Framatube.org)
            // don't support the old cipher suites.
            // https://github.com/square/okhttp/issues/4053#issuecomment-402579554
            final List<CipherSuite> cipherSuites =
                    new ArrayList<>(ConnectionSpec.MODERN_TLS.cipherSuites());
            cipherSuites.add(CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256);
            cipherSuites.add(CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384);
            cipherSuites.add(CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256);
            cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA);
            cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA);
            final ConnectionSpec legacyTLS = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .cipherSuites(cipherSuites.toArray(new CipherSuite[0]))
                    .build();

            builder.connectionSpecs(Arrays.asList(legacyTLS, ConnectionSpec.CLEARTEXT));
        } catch (final KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }


}
