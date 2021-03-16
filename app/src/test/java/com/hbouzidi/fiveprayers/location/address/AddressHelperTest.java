package com.hbouzidi.fiveprayers.location.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.core.app.ApplicationProvider;

import com.hbouzidi.fiveprayers.exceptions.LocationException;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIService;
import com.hbouzidi.fiveprayers.location.osm.NominatimAddress;
import com.hbouzidi.fiveprayers.location.osm.NominatimReverseGeocodeResponse;
import com.hbouzidi.fiveprayers.network.NetworkUtil;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */

@RunWith(RobolectricTestRunner.class)
@Config(maxSdk = 28)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*"})
@PrepareForTest({AddressHelper.class, NetworkUtil.class, NominatimAPIService.class,
        Geocoder.class, PreferencesHelper.class})
public class AddressHelperTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    Context mockContext;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor mockEditor;
    NominatimAPIService nominatimAPIService;
    NetworkUtil mNetworkUtil;
    Geocoder mGeocoder;
    AddressHelper addressHelper;

    @Before
    public void before() throws Exception {
        this.sharedPrefs = PowerMockito.mock(SharedPreferences.class);
        this.mockEditor = PowerMockito.mock(SharedPreferences.Editor.class);
        this.mockContext = PowerMockito.mock(Context.class);
        this.nominatimAPIService = PowerMockito.mock(NominatimAPIService.class);
        this.mNetworkUtil = PowerMockito.mock(NetworkUtil.class);
        this.mGeocoder = PowerMockito.mock(Geocoder.class);
        this.addressHelper = PowerMockito.mock(AddressHelper.class);

//        PowerMockito
//                .doReturn(sharedPrefs)
//                .when(Context.class, "getSharedPreferences", anyString(), anyInt());
//
//        PowerMockito.when(mockContext.getSharedPreferences(anyString(), anyInt()).edit()).thenReturn(mockEditor);

        PowerMockito.mockStatic(PreferencesHelper.class);
    }

    @Test
    public void getAddressFromLocation_when_location_is_null() {
        Context applicationContext = ApplicationProvider.getApplicationContext();

        TestObserver<Address> addressTestObserver = new TestObserver<>();
        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(null, applicationContext);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.assertNotComplete();
        addressTestObserver.assertError(LocationException.class);
    }

    @Test
    public void getAddressFromLocation_when_address_is_not_obsolete() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(45.2);
        lastKnownAddress.setLongitude(-12.2);
        lastKnownAddress.setLocality("Colombes");

        Context applicationContext = ApplicationProvider.getApplicationContext();

        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(newLocation, applicationContext);

        PowerMockito.spy(AddressHelper.class);

        PowerMockito
                .doReturn(lastKnownAddress)
                .when(PreferencesHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(false)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals(lastKnownAddress, address);
            assertEquals("Colombes", address.getLocality());
            return true;
        });
    }

    @Test
    public void getAddressFromLocation_when_address_is_obsolete_and_geocoder_available() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(45.2);
        lastKnownAddress.setLongitude(-12.2);
        lastKnownAddress.setLocality("Colombes");

        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setLocality("Paris");

        Context applicationContext = ApplicationProvider.getApplicationContext();

        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(newLocation, applicationContext);

        PowerMockito.spy(AddressHelper.class);

        PowerMockito
                .doReturn(lastKnownAddress)
                .when(PreferencesHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(address)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(geoCoderAddress -> {
            assertEquals(address, geoCoderAddress);
            assertEquals("Paris", address.getLocality());
            return true;
        });
    }

    @Test
    public void getAddressFromLocation_when_address_is_obsolete_and_nominatim_available() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(45.2);
        lastKnownAddress.setLongitude(-12.2);
        lastKnownAddress.setLocality("Colombes");

        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setLocality("Marseille");

        Context applicationContext = ApplicationProvider.getApplicationContext();

        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(newLocation, applicationContext);

        PowerMockito.spy(AddressHelper.class);

        PowerMockito
                .doReturn(lastKnownAddress)
                .when(PreferencesHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doReturn(address)
                .when(AddressHelper.class, "getNominatimAddress", anyDouble(), anyDouble(), any());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(nominatimAddress -> {
            assertEquals(address, nominatimAddress);
            assertEquals("Marseille", address.getLocality());
            return true;
        });
    }

    @Test
    public void getAddressFromLocation_when_address_is_obsolete_and_only_has_known_address__available() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(45.2);
        lastKnownAddress.setLongitude(-12.2);
        lastKnownAddress.setLocality("Colombes");

        Context applicationContext = ApplicationProvider.getApplicationContext();

        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(newLocation, applicationContext);

        PowerMockito.spy(AddressHelper.class);

        PowerMockito
                .doReturn(lastKnownAddress)
                .when(PreferencesHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getNominatimAddress", anyDouble(), anyDouble(), any());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(address -> {
            assertEquals(lastKnownAddress, address);
            assertEquals("Colombes", address.getLocality());
            return true;
        });
    }

    @Test
    public void getAddressFromLocation_when_address_is_obsolete_no_address_available() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());

        Context applicationContext = ApplicationProvider.getApplicationContext();

        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(newLocation, applicationContext);

        PowerMockito.spy(AddressHelper.class);

        PowerMockito
                .doReturn(lastKnownAddress)
                .when(PreferencesHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getNominatimAddress", anyDouble(), anyDouble(), any());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertError(LocationException.class);
    }

    @Test
    public void getAddressFromLocation_when_api_throws_error() throws Exception {
        TestObserver<Address> addressTestObserver = new TestObserver<>();

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());

        Context applicationContext = ApplicationProvider.getApplicationContext();

        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(newLocation, applicationContext);

        PowerMockito.spy(AddressHelper.class);

        PowerMockito
                .doReturn(lastKnownAddress)
                .when(PreferencesHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doThrow(new RuntimeException())
                .when(AddressHelper.class, "getNominatimAddress", anyDouble(), anyDouble(), any());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertError(LocationException.class);
    }

    @Test
    public void isAddressObsolete() throws Exception {
        //Given
        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryCode("UK");

        //When
        boolean isAddressObsolete = Whitebox
                .invokeMethod(new AddressHelper(),
                        "isAddressObsolete", lastKnownAddress, 51.508515, -0.1254872);

        //Then
        assertFalse(isAddressObsolete);

        //When
        boolean result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "isAddressObsolete", lastKnownAddress, 25.2048493, 55.2707828);

        //Then
        assertTrue(result);
    }

    @Test
    public void isAddressObsolete_when_locality_is_null() throws Exception {
        //Given
        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);


        //When
        boolean result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "isAddressObsolete", lastKnownAddress, 51.508515, -0.1254872);

        //Then
        assertTrue(result);
    }

    @Test
    public void getNominatimAddress_when_network_is_available() throws Exception {
        //Given
        Context applicationContext = ApplicationProvider.getApplicationContext();
        PowerMockito.mockStatic(NetworkUtil.class);
        PowerMockito.mockStatic(NominatimAPIService.class);
        NominatimAddress address = new NominatimAddress();
        address.setTown("London");
        address.setCountryCode("UK");
        address.setCountry("United Kingdom");
        address.setPostcode("99100");

        NominatimReverseGeocodeResponse nominatimReverseGeocodeResponse = new NominatimReverseGeocodeResponse();
        nominatimReverseGeocodeResponse.setAddress(address);
        nominatimReverseGeocodeResponse.setLat(-0.1254872);
        nominatimReverseGeocodeResponse.setLon(51.508515);

        PowerMockito.when(NominatimAPIService.getInstance()).thenReturn(nominatimAPIService);

        PowerMockito.when(nominatimAPIService.getAddressFromLocation(anyDouble(), anyDouble()))
                .thenReturn(nominatimReverseGeocodeResponse);
        PowerMockito.when(NetworkUtil.isNetworkAvailable(any())).thenReturn(true);

        //When
        Address result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "getNominatimAddress", 51.508515, -0.1254872, applicationContext);

        //Then
        assertNotNull(result);
        assertEquals("London", result.getLocality());
        assertEquals("UK", result.getCountryCode());
        assertEquals("United Kingdom", result.getCountryName());
        assertEquals("99100", result.getPostalCode());
    }

    @Test
    public void getNominatimAddress_when_network_is_Not_available() throws Exception {
        //Given
        PowerMockito.mockStatic(NetworkUtil.class);
        PowerMockito.when(NetworkUtil.isNetworkAvailable(mockContext)).thenReturn(false);

        //When
        Address result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "getNominatimAddress", 51.508515, -0.1254872, mockContext);

        //Then
        assertNull(result);
    }
}