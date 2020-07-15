package com.bouzidi.prayertimes.location.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.exceptions.LocationException;
import com.bouzidi.prayertimes.preferences.PreferencesConstants;
import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.core.Single;

import static android.content.Context.MODE_PRIVATE;

public class LocationHelper {

    public static Single<Location> getLocation(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);

        final double lastKnownLatitude = UserPreferencesUtils.getDouble(sharedPreferences, PreferencesConstants.LAST_KNOWN_LATITUDE, 0);
        final double lastKnownLongitude = UserPreferencesUtils.getDouble(sharedPreferences, PreferencesConstants.LAST_KNOWN_LONGITUDE, 0);

        GPSTracker gpsTracker = new GPSTracker(context);

        return Single.create(emitter -> {
            if (gpsTracker.canGetLocation()) {
                Location newLocation = gpsTracker.getLocation();

                if (newLocation != null) {
                    Log.i(LocationHelper.class.getName(), "Get location from tracker");

                    emitter.onSuccess(newLocation);
                } else if (lastKnownLatitude > 0 && lastKnownLongitude > 0) {
                    Log.w(LocationHelper.class.getName(), "Cannot get location from tracker, use last known location");

                    Location lastKnownLocation = getLastKnownLocation(lastKnownLatitude, lastKnownLongitude);
                    emitter.onSuccess(lastKnownLocation);
                }
            } else if (lastKnownLatitude > 0 && lastKnownLongitude > 0) {

                Location lastKnownLocation = getLastKnownLocation(lastKnownLatitude, lastKnownLongitude);
                emitter.onSuccess(lastKnownLocation);

                Log.w(LocationHelper.class.getName(), "Location tracker not available, using last known location");
            } else {
                emitter.onError(new LocationException(context.getResources().getString(R.string.location_service_unavailable)));
                Log.e(LocationHelper.class.getName(), "Location tracker not available");
            }
        });
    }

    @NotNull
    private static Location getLastKnownLocation(double lastKnownLatitude, double lastKnownLongitude) {
        Location lastKnownLocation = new Location("");
        lastKnownLocation.setLatitude(lastKnownLatitude);
        lastKnownLocation.setLongitude(lastKnownLongitude);
        return lastKnownLocation;
    }
}
