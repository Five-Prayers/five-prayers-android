package com.bouzidi.prayertimes.location.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.core.app.ApplicationProvider;

import com.bouzidi.prayertimes.exceptions.LocationException;
import com.bouzidi.prayertimes.location.arcgis.ArcgisAPIService;
import com.bouzidi.prayertimes.location.arcgis.ArcgisAddressResponse;
import com.bouzidi.prayertimes.location.arcgis.ArcgisLocation;
import com.bouzidi.prayertimes.location.arcgis.ArcgisReverseGeocodeResponse;
import com.bouzidi.prayertimes.network.NetworkUtil;
import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.Locale;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;


@RunWith(RobolectricTestRunner.class)
@Config(maxSdk = 29)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*"})
@PrepareForTest({AddressHelper.class, NetworkUtil.class, ArcgisAPIService.class, Geocoder.class})
public class AddressHelperTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    Context mockContext;
    @Mock
    SharedPreferences sharedPrefs;
    @Mock
    SharedPreferences.Editor mockEditor;
    @Mock
    ArcgisAPIService mArcgisAPIService;
    @Mock
    NetworkUtil mNetworkUtil;
    @Mock
    Geocoder mGeocoder;
    @Mock
    AddressHelper addressHelper;

    @Before
    public void before() {
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        this.mockEditor = Mockito.mock(SharedPreferences.Editor.class);
        this.mockContext = Mockito.mock(Context.class);
        this.mArcgisAPIService = Mockito.mock(ArcgisAPIService.class);
        this.mNetworkUtil = Mockito.mock(NetworkUtil.class);
        this.mGeocoder = Mockito.mock(Geocoder.class);
        this.addressHelper = Mockito.mock(AddressHelper.class);

        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt()).edit()).thenReturn(mockEditor);
    }

    @Test
    public void getAddressFromLocation_when_location_is_null() {
        TestObserver<Address> addressTestObserver = new TestObserver<>();
        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(null, mockContext);

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
                .when(AddressHelper.class, "getLastKnownAddress", applicationContext);

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
                .when(AddressHelper.class, "getLastKnownAddress", applicationContext);

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
    public void getAddressFromLocation_when_address_is_obsolete_and_arcgis_available() throws Exception {
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
                .when(AddressHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doReturn(address)
                .when(AddressHelper.class, "getArcgisAddress", anyDouble(), anyDouble(), any());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertComplete();
        addressTestObserver.assertValue(arcgisAddress -> {
            assertEquals(address, arcgisAddress);
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
                .when(AddressHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getArcgisAddress", anyDouble(), anyDouble(), any());

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
                .when(AddressHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getArcgisAddress", anyDouble(), anyDouble(), any());

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
                .when(AddressHelper.class, "getLastKnownAddress", applicationContext);

        PowerMockito
                .doReturn(true)
                .when(AddressHelper.class, "isAddressObsolete", any(), anyDouble(), anyDouble());

        PowerMockito
                .doReturn(null)
                .when(AddressHelper.class, "getGeocoderAddresses", anyDouble(), anyDouble(), any());

        PowerMockito
                .doThrow(new RuntimeException())
                .when(AddressHelper.class, "getArcgisAddress", anyDouble(), anyDouble(), any());

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.await();
        addressTestObserver.assertError(LocationException.class);
    }

    @Test
    public void getAddressFromLocation() throws Exception {
        //Given
        Context applicationContext = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("location", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("last_known_locality", "Colombes");
        edit.putString("last_known_country", "France");
        UserPreferencesUtils.putDouble(edit, "last_known_latitude", 11.6);
        UserPreferencesUtils.putDouble(edit, "last_known_longitude", -13.65587);
        edit.commit();

        //When
        Address lastKnownAddress = Whitebox
                .invokeMethod(new AddressHelper(), "getLastKnownAddress", applicationContext);

        //Then
        assertNotNull(lastKnownAddress);
        assertEquals("Colombes", lastKnownAddress.getLocality());
        assertEquals("France", lastKnownAddress.getCountryName());
        assertEquals(11.6, lastKnownAddress.getLatitude(), 0);
        assertEquals(-13.65587, lastKnownAddress.getLongitude(), 0);
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
    public void getArcgisAddress_when_network_is_available() throws Exception {
        //Given
        Context applicationContext = ApplicationProvider.getApplicationContext();
        PowerMockito.mockStatic(NetworkUtil.class);
        PowerMockito.mockStatic(ArcgisAPIService.class);
        ArcgisAddressResponse address = new ArcgisAddressResponse();
        address.setCity("London");
        address.setCountryCode("UK");
        address.setPostal("99100");

        ArcgisLocation location = new ArcgisLocation();
        location.setX(51.508515);
        location.setY(-0.1254872);

        ArcgisReverseGeocodeResponse arcgisReverseGeocodeResponse = new ArcgisReverseGeocodeResponse();
        arcgisReverseGeocodeResponse.setAddress(address);
        arcgisReverseGeocodeResponse.setLocation(location);

        Mockito.when(ArcgisAPIService.getInstance()).thenReturn(mArcgisAPIService);
        Mockito.when(mArcgisAPIService.getAddressFromLocation(anyDouble(), anyDouble()))
                .thenReturn(arcgisReverseGeocodeResponse);
        Mockito.when(NetworkUtil.isNetworkAvailable(any())).thenReturn(true);

        //When
        Address result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "getArcgisAddress", 51.508515, -0.1254872, applicationContext);

        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("location", MODE_PRIVATE);
        String lastKnownCountry = sharedPreferences.getString("last_known_country", null);
        String lastKnownLocality = sharedPreferences.getString("last_known_locality", null);
        double lastKnownLatitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_latitude", 0);
        double lastKnownLongitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_longitude", 0);

        //Then
        assertNotNull(result);
        assertEquals("London", result.getLocality());
        assertEquals("UK", result.getCountryCode());
        assertEquals("UK", result.getCountryName());
        assertEquals("99100", result.getPostalCode());
        assertEquals("London", lastKnownLocality);
        assertEquals("UK", lastKnownCountry);
        assertEquals(51.508515, lastKnownLatitude, 0);
        assertEquals(-0.1254872, lastKnownLongitude, 0);
    }

    @Test
    public void getArcgisAddress_when_network_is_Not_available() throws Exception {
        //Given
        PowerMockito.mockStatic(NetworkUtil.class);
        Mockito.when(NetworkUtil.isNetworkAvailable(mockContext)).thenReturn(false);

        //When
        Address result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "getArcgisAddress", 51.508515, -0.1254872, mockContext);

        //Then
        assertNull(result);
    }

    @Test
    public void getGeocoderAddresses() throws Exception {
        //Given
        Context applicationContext = ApplicationProvider.getApplicationContext();
        Address address = new Address(Locale.getDefault());
        address.setLatitude(51.508515);
        address.setLongitude(-0.1254872);
        address.setLocality("London");
        address.setCountryCode("UK");
        address.setCountryName("United Kingdom");
        address.setPostalCode("99100");

        PowerMockito.whenNew(Geocoder.class).withArguments(applicationContext, Locale.getDefault()).thenReturn(mGeocoder);

        Mockito.when(mGeocoder.getFromLocation(51.508515, -0.1254872, 1))
                .thenReturn(Collections.singletonList(address));
        //When
        Address result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "getGeocoderAddresses", 51.508515, -0.1254872, applicationContext);


        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("location", MODE_PRIVATE);
        String lastKnownCountry = sharedPreferences.getString("last_known_country", null);
        String lastKnownLocality = sharedPreferences.getString("last_known_locality", null);
        double lastKnownLatitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_latitude", 0);
        double lastKnownLongitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_longitude", 0);

        //Then
        assertNotNull(result);
        assertEquals("London", result.getLocality());
        assertEquals("UK", result.getCountryCode());
        assertEquals("United Kingdom", result.getCountryName());
        assertEquals("99100", result.getPostalCode());
        assertEquals("London", lastKnownLocality);
        assertEquals("United Kingdom", lastKnownCountry);
        assertEquals(51.508515, lastKnownLatitude, 0);
        assertEquals(-0.1254872, lastKnownLongitude, 0);
    }

    @Test
    public void getGeocoderAddresses_when_geocoder_return_empty_list() throws Exception {
        //Given
        PowerMockito.whenNew(Geocoder.class).withArguments(mockContext, Locale.getDefault()).thenReturn(mGeocoder);

        Mockito.when(mGeocoder.getFromLocation(51.508515, -0.1254872, 1))
                .thenReturn(Collections.emptyList());
        //When
        Address result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "getGeocoderAddresses", 51.508515, -0.1254872, mockContext);

        //Then
        assertNull(result);
    }

    @Test
    public void getGeocoderAddresses_when_geocoder_return_null() throws Exception {
        //Given
        PowerMockito.whenNew(Geocoder.class).withArguments(mockContext, Locale.getDefault()).thenReturn(mGeocoder);

        Mockito.when(mGeocoder.getFromLocation(51.508515, -0.1254872, 1))
                .thenReturn(null);
        //When
        Address result = Whitebox
                .invokeMethod(new AddressHelper(),
                        "getGeocoderAddresses", 51.508515, -0.1254872, mockContext);

        //Then
        assertNull(result);
    }
}