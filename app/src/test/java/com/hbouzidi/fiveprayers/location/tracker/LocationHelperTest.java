package com.hbouzidi.fiveprayers.location.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.core.app.ApplicationProvider;

import com.hbouzidi.fiveprayers.exceptions.LocationException;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.utils.UserPreferencesUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */

@RunWith(RobolectricTestRunner.class)
@Config(minSdk = 18, maxSdk = 28)
public class LocationHelperTest {

    Context applicationContext;

    @Before
    public void before() {
        applicationContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void should_get_new_location_when_tracker_available_and_new_location_is_not_null() throws Exception {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        LocationManager locationManager = (LocationManager) applicationContext
                .getSystemService(LOCATION_SERVICE);

        ShadowLocationManager shadowLocationManager = Shadows.shadowOf(locationManager);
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
        shadowLocationManager.setLocationEnabled(true);
        shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, newLocation);

        Single<Location> locationSingle = LocationHelper.getLocation(applicationContext);

        TestObserver<Location> locationTestObserver = new TestObserver<>();
        locationSingle.subscribe(locationTestObserver);

        locationTestObserver.await();
        locationTestObserver.assertComplete();
        locationTestObserver.assertValue(newLocation);
    }

    @Test
    public void should_get_last_known_location_when_tracker_available_and_new_location_is_null() throws Exception {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        UserPreferencesUtils.putDouble(edit, PreferencesConstants.LAST_KNOWN_LATITUDE, 11.6);
        UserPreferencesUtils.putDouble(edit, PreferencesConstants.LAST_KNOWN_LONGITUDE, 13.65587);
        edit.commit();

        LocationManager locationManager = (LocationManager) applicationContext
                .getSystemService(LOCATION_SERVICE);

        ShadowLocationManager shadowLocationManager = Shadows.shadowOf(locationManager);
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
        shadowLocationManager.setLocationEnabled(true);

        Single<Location> locationSingle = LocationHelper.getLocation(applicationContext);

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
    public void should_get_last_known_location_when_tracker_not_available_and_last_location_is_not_null() throws Exception {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        UserPreferencesUtils.putDouble(edit, PreferencesConstants.LAST_KNOWN_LATITUDE, 11.6);
        UserPreferencesUtils.putDouble(edit, PreferencesConstants.LAST_KNOWN_LONGITUDE, 13.65587);
        edit.commit();

        LocationManager locationManager = (LocationManager) applicationContext
                .getSystemService(LOCATION_SERVICE);

        ShadowLocationManager shadowLocationManager = Shadows.shadowOf(locationManager);
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, false);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, false);
        shadowLocationManager.setLocationEnabled(false);

        Single<Location> locationSingle = LocationHelper.getLocation(applicationContext);

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
    public void should_throw_error_when_tracker_is_available_and_new_location_is_null() {
        LocationManager locationManager = (LocationManager) applicationContext
                .getSystemService(LOCATION_SERVICE);

        ShadowLocationManager shadowLocationManager = Shadows.shadowOf(locationManager);
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
        shadowLocationManager.setLocationEnabled(true);

        Single<Location> locationSingle = LocationHelper.getLocation(applicationContext);

        TestObserver<Location> locationTestObserver = new TestObserver<>();
        locationSingle.subscribe(locationTestObserver);

        locationTestObserver.assertNotComplete();
        locationTestObserver.assertError(LocationException.class);
    }

    @Test
    public void should_throw_error_when_tracker_not_available_and_last_location_is_null() throws Exception {
        LocationManager locationManager = (LocationManager) applicationContext
                .getSystemService(LOCATION_SERVICE);

        ShadowLocationManager shadowLocationManager = Shadows.shadowOf(locationManager);
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, false);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, false);
        shadowLocationManager.setLocationEnabled(false);

        Single<Location> locationSingle = LocationHelper.getLocation(applicationContext);

        TestObserver<Location> locationTestObserver = new TestObserver<>();
        locationSingle.subscribe(locationTestObserver);

        locationTestObserver.assertNotComplete();
        locationTestObserver.assertError(LocationException.class);
    }
}