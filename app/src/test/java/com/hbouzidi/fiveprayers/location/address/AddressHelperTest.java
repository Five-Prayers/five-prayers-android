package com.hbouzidi.fiveprayers.location.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;
import com.hbouzidi.fiveprayers.exceptions.LocationException;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIService;
import com.hbouzidi.fiveprayers.location.osm.NominatimAddress;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.shadows.ShadowGeocoder;

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
import java.util.Locale;
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

import static io.appflate.restmock.utils.RequestMatchers.pathContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */

@RunWith(RobolectricTestRunner.class)
@Config(minSdk = 18, maxSdk = 28, application = FakeFivePrayerApplication.class, shadows = {ShadowGeocoder.class})
public class AddressHelperTest {

    Context applicationContext;

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();
    private AddressHelper addressHelper;
    private PreferencesHelper preferencesHelper;

    @Before
    public void before() {
        RESTMockServerStarter.startSync(new JVMFileParser(), new AndroidLogger());

        applicationContext = ApplicationProvider.getApplicationContext();
        preferencesHelper = new PreferencesHelper(applicationContext);

        NominatimAPIService nominatimAPIService = new NominatimAPIService(provideRetrofit());
        addressHelper = new AddressHelper(applicationContext, nominatimAPIService, preferencesHelper);
    }

    @After
    public void tearDown() throws IOException {
        ShadowGeocoder.setIsPresent(true);
        ShadowGeocoder.setLocalityIsNull(false);
        RESTMockServer.shutdown();
    }

    @Test
    public void should_throw_error_when_location_is_null() {
        TestObserver<Address> addressTestObserver = new TestObserver<>();
        Single<Address> addressSingle = addressHelper.getAddressFromLocation(null);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.assertNotComplete();
        addressTestObserver.assertError(LocationException.class);
    }

    @Test
    public void should_get_last_known_address_when_location_set_manually() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(-6.8498129);
        newLocation.setLongitude(33.9715904);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putBoolean(PreferencesConstants.LOCATION_SET_MANUALLY_PREFERENCE, true);
        editor.commit();

        preferencesHelper.updateAddressPreferences(lastKnownAddress);

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals("London", address.getLocality());
            assertEquals("United Kindom", address.getCountryName());
            return true;
        });
    }

    @Test
    public void should_get_address_when_geocoder_available_and_address_is_not_obsolete() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(51.5073509);
        newLocation.setLongitude(-0.1277583);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        preferencesHelper.updateAddressPreferences(lastKnownAddress);

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals("London", address.getLocality());
            assertEquals("United Kindom", address.getCountryName());
            return true;
        });
    }

    @Test
    public void should_get_address_from_geocoder_when_address_is_obsolete_and_geocoder_available() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(48.9220615);
        newLocation.setLongitude(2.2533313);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        preferencesHelper.updateAddressPreferences(lastKnownAddress);

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals("Colombes", address.getLocality());
            assertEquals("France", address.getCountryName());
            return true;
        });
    }

    @Test
    public void should_get_address_from_geocoder_when_last_known_locality_is_null_and_geocoder_available() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(51.5073509);
        newLocation.setLongitude(-0.1277583);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        preferencesHelper.updateAddressPreferences(lastKnownAddress);

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals("Colombes", address.getLocality());
            assertEquals("France", address.getCountryName());
            return true;
        });
    }

    @Test
    public void should_get_address_from_nominatim_when_address_is_obsolete_and_geocoder_available_but_locality_is_null() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(-6.8498129);
        newLocation.setLongitude(33.9715904);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        NominatimAddress nominatimAddress = new NominatimAddress();
        nominatimAddress.setCity("Rabat");
        nominatimAddress.setCountry("Morocco");
        nominatimAddress.setCountryCode("MA");

        preferencesHelper.updateAddressPreferences(lastKnownAddress);
        ShadowGeocoder.setLocalityIsNull(true);

        RESTMockServer.reset();
        RESTMockServer
                .whenGET(pathContains("/reverse"))
                .thenReturnFile(200, "responses/nominatim_response.json");

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals("Rabat", address.getLocality());
            assertEquals("Morocco", address.getCountryName());
            return true;
        });
    }

    @Test
    public void should_get_address_from_nominatim_when_address_is_obsolete_and_geocoder_not_available() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(-6.8498129);
        newLocation.setLongitude(33.9715904);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        NominatimAddress nominatimAddress = new NominatimAddress();
        nominatimAddress.setCity("Rabat");
        nominatimAddress.setCountry("Morocco");
        nominatimAddress.setCountryCode("MA");

        preferencesHelper.updateAddressPreferences(lastKnownAddress);
        ShadowGeocoder.setIsPresent(false);

        RESTMockServer.reset();
        RESTMockServer
                .whenGET(pathContains("/reverse"))
                .thenReturnFile(200, "responses/nominatim_response.json");

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals("Rabat", address.getLocality());
            assertEquals("Morocco", address.getCountryName());
            return true;
        });
    }

    @Test
    public void should_get_offline_address_when_address_is_obsolete_and_geocoder_nominatim_not_availables() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(-6.8498129);
        newLocation.setLongitude(33.9715904);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        NominatimAddress nominatimAddress = new NominatimAddress();
        nominatimAddress.setCity("Rabat");
        nominatimAddress.setCountry("Morocco");
        nominatimAddress.setCountryCode("MA");

        preferencesHelper.updateAddressPreferences(lastKnownAddress);
        ShadowGeocoder.setIsPresent(false);

        RESTMockServer.reset();
        RESTMockServer
                .whenGET(pathContains("/reverse"))
                .thenReturnFile(200, "responses/nominatim_error_response.json");

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertNull(address.getLocality());
            assertNull(address.getCountryName());
            assertEquals(-6.8498129, address.getLatitude(), 0);
            assertEquals(33.9715904, address.getLongitude(), 0);
            return true;
        });
    }

    @Test
    public void should_get_offline_address_when_address_is_obsolete_and_geocoder_not_available_nominatim_error() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(-6.8498129);
        newLocation.setLongitude(33.9715904);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        NominatimAddress nominatimAddress = new NominatimAddress();
        nominatimAddress.setCity("Rabat");
        nominatimAddress.setCountry("Morocco");
        nominatimAddress.setCountryCode("MA");

        preferencesHelper.updateAddressPreferences(lastKnownAddress);
        ShadowGeocoder.setIsPresent(false);

        RESTMockServer.reset();
        RESTMockServer
                .whenGET(pathContains("/reverse"))
                .thenReturnFile(403, "responses/nominatim_error_response.json");

        Single<Address> addressSingle = addressHelper.getAddressFromLocation(newLocation);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertNull(address.getLocality());
            assertNull(address.getCountryName());
            assertEquals(-6.8498129, address.getLatitude(), 0);
            assertEquals(33.9715904, address.getLongitude(), 0);
            return true;
        });
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