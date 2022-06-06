package com.hbouzidi.fiveprayers.location.address;

import static org.junit.Assert.assertEquals;
import static io.appflate.restmock.utils.RequestMatchers.pathContains;

import android.content.Context;
import android.location.Address;

import androidx.test.core.app.ApplicationProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;
import com.hbouzidi.fiveprayers.location.photon.PhotonAPIService;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.shadows.ShadowGeocoder;
import com.hbouzidi.fiveprayers.utils.LocaleHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.appflate.restmock.JVMFileParser;
import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RESTMockServerStarter;
import io.appflate.restmock.android.AndroidLogger;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */

@RunWith(RobolectricTestRunner.class)
@Config(minSdk = 28, maxSdk = 28, application = FakeFivePrayerApplication.class, shadows = {ShadowGeocoder.class})
public class AddressSearchServiceTest {

    Context applicationContext;

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();
    private AddressSearchService addressSearchService;

    @Before
    public void before() {
        RESTMockServerStarter.startSync(new JVMFileParser(), new AndroidLogger());

        applicationContext = ApplicationProvider.getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(applicationContext);

        LocaleHelper localeHelper = new LocaleHelper(preferencesHelper);

        PhotonAPIService photonAPIService = new PhotonAPIService(provideRetrofit());
        addressSearchService = new AddressSearchService(photonAPIService, localeHelper, applicationContext);
    }

    @After
    public void tearDown() throws IOException {
        RESTMockServer.shutdown();
        ShadowGeocoder.reset();
    }

    @Test
    public void should_search_address_when_geocoder_available() throws Exception {
        TestObserver<List<Address>> addressTestObserver = new TestObserver<>();

        Single<List<Address>> addressSingle = addressSearchService.searchForLocation("berlin", 1);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(addressList -> {
            assertEquals(1, addressList.size());
            return true;
        });
    }

    @Test
    public void should_search_address_when_geocoder_not_available() throws Exception {
        TestObserver<List<Address>> addressTestObserver = new TestObserver<>();

        Single<List<Address>> addressSingle = addressSearchService.searchForLocation("berlin", 3);

        ShadowGeocoder.setIsPresent(false);

        RESTMockServer.reset();
        RESTMockServer
                .whenGET(pathContains("/?q=berlin"))
                .thenReturnFile(200, "responses/photon_response.json");

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(addressList -> {
            assertEquals(2, addressList.size());
            return true;
        });
    }

    @Test
    public void should_search_address_when_geocoder_available_and_throw_exception() throws Exception {
        TestObserver<List<Address>> addressTestObserver = new TestObserver<>();

        Single<List<Address>> addressSingle = addressSearchService.searchForLocation("berlin", 3);

        ShadowGeocoder.setThrowException(true);

        RESTMockServer.reset();
        RESTMockServer
                .whenGET(pathContains("/?q=berlin"))
                .thenReturnFile(200, "responses/photon_response.json");

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(addressList -> {
            assertEquals(2, addressList.size());
            return true;
        });
    }

    @Test
    public void should_not_get_address_when_nor_geocoder_nor_photon_available() throws Exception {
        TestObserver<List<Address>> addressTestObserver = new TestObserver<>();

        Single<List<Address>> addressSingle = addressSearchService.searchForLocation("berlin", 3);

        ShadowGeocoder.setThrowException(true);

        RESTMockServer.reset();
        RESTMockServer
                .whenGET(pathContains("/?q=berlin"))
                .thenReturnFile(404, "responses/photon_response.json");

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertNotComplete();
    }

    private Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .baseUrl(RESTMockServer.getUrl())
                .client(provideNonCachedOkHttpClient())
                .build();
    }

    private Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private OkHttpClient provideNonCachedOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        return builder
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}