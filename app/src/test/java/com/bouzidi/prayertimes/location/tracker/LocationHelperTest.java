package com.bouzidi.prayertimes.location.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.core.app.ApplicationProvider;

import com.bouzidi.prayertimes.exceptions.LocationException;
import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import org.junit.Assert;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(RobolectricTestRunner.class)
@Config(maxSdk = 28)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*"})
@PrepareForTest({LocationHelper.class, UserPreferencesUtils.class})
public class LocationHelperTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    Context mockContext;

    @Mock
    GPSTracker gpsTracker;

    @Mock
    SharedPreferences sharedPrefs;

    @Mock
    UserPreferencesUtils userPreferencesUtils;

    @Before
    public void before() {
        this.gpsTracker = Mockito.mock(GPSTracker.class);
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        this.userPreferencesUtils = Mockito.mock(UserPreferencesUtils.class);
        this.mockContext = Mockito.mock(Context.class);

        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
    }

    @Test
    public void getLocation_when_tracker_available_and_location_is_not_null() throws Exception {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Mockito.when(gpsTracker.canGetLocation()).thenReturn(true);
        Mockito.when(gpsTracker.getLocation()).thenReturn(newLocation);
        Context applicationContext = ApplicationProvider.getApplicationContext();
        PowerMockito.whenNew(GPSTracker.class).withArguments(applicationContext).thenReturn(gpsTracker);

        Single<Location> locationSingle = LocationHelper.getLocation(applicationContext);

        TestObserver<Location> locationTestObserver = new TestObserver<>();
        locationSingle.subscribe(locationTestObserver);

        locationTestObserver.await();
        locationTestObserver.assertComplete();
        locationTestObserver.assertValue(newLocation);
    }

    @Test
    public void getLocation_when_tracker_available_and_location_is_null() throws Exception {
        Mockito.when(gpsTracker.canGetLocation()).thenReturn(true);
        Mockito.when(gpsTracker.getLocation()).thenReturn(null);

        PowerMockito.whenNew(GPSTracker.class).withArguments(mockContext).thenReturn(gpsTracker);
        PowerMockito.mockStatic(UserPreferencesUtils.class);

        Mockito.when(UserPreferencesUtils.getDouble(sharedPrefs, "last_known_latitude", 0)).thenReturn(11.6);
        Mockito.when(UserPreferencesUtils.getDouble(sharedPrefs, "last_known_longitude", 0)).thenReturn(13.65587);


        Single<Location> locationSingle = LocationHelper.getLocation(mockContext);

        TestObserver<Location> locationTestObserver = new TestObserver<>();
        locationSingle.subscribe(locationTestObserver);

        locationTestObserver.await();
        locationTestObserver.assertComplete();
        locationTestObserver.assertValue(location -> {
            Assert.assertEquals(11.6, location.getLatitude(), 0);
            Assert.assertEquals(13.65587, location.getLongitude(), 0);
            return true;
        });
    }

    @Test
    public void getLocation_when_tracker_not_available_and_last_location_is_not_null() throws Exception {
        PowerMockito.whenNew(GPSTracker.class).withArguments(mockContext).thenReturn(gpsTracker);
        PowerMockito.mockStatic(UserPreferencesUtils.class);

        Mockito.when(gpsTracker.canGetLocation()).thenReturn(false);

        Mockito.when(UserPreferencesUtils.getDouble(sharedPrefs, "last_known_latitude", 0)).thenReturn(11.6);
        Mockito.when(UserPreferencesUtils.getDouble(sharedPrefs, "last_known_longitude", 0)).thenReturn(13.65587);

        Single<Location> locationSingle = LocationHelper.getLocation(mockContext);

        TestObserver<Location> locationTestObserver = new TestObserver<>();
        locationSingle.subscribe(locationTestObserver);

        locationTestObserver.await();
        locationTestObserver.assertComplete();
        locationTestObserver.assertValue(location -> {
            Assert.assertEquals(11.6, location.getLatitude(), 0);
            Assert.assertEquals(13.65587, location.getLongitude(), 0);
            return true;
        });
    }

    @Test
    public void getLocation_when_tracker_not_available_and_last_location_is_null() throws Exception {
        PowerMockito.whenNew(GPSTracker.class).withArguments(mockContext).thenReturn(gpsTracker);
        PowerMockito.mockStatic(UserPreferencesUtils.class);

        Mockito.when(gpsTracker.canGetLocation()).thenReturn(false);

        Mockito.when(UserPreferencesUtils.getDouble(sharedPrefs, "last_known_latitude", 0)).thenReturn(0.);
        Mockito.when(UserPreferencesUtils.getDouble(sharedPrefs, "last_known_longitude", 0)).thenReturn(0.);

        Single<Location> locationSingle = LocationHelper.getLocation(mockContext);

        TestObserver<Location> locationTestObserver = new TestObserver<>();
        locationSingle.subscribe(locationTestObserver);

        locationTestObserver.assertNotComplete();
        locationTestObserver.assertError(LocationException.class);
    }
}