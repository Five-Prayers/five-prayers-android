package com.bouzidi.prayertimes.location.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import static android.content.Context.MODE_PRIVATE;

public class LocationHelper {

    public static Location getLocation(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("location", MODE_PRIVATE);

        final double lastKnownLatitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_latitude", 0);
        final double lastKnownLongitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_longitude", 0);

        GPSTracker gpsTracker = new GPSTracker(context);

        if (gpsTracker.canGetLocation()) {
            Location newLocation = gpsTracker.getLocation();

            double newLatitude = newLocation.getLatitude();
            double newLongitude = newLocation.getLongitude();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            UserPreferencesUtils.putDouble(editor, "last_known_latitude", newLatitude);
            UserPreferencesUtils.putDouble(editor, "last_known_longitude", newLongitude);

            editor.apply();

            return newLocation;
        } else if (lastKnownLatitude >= 0 || lastKnownLongitude >= 0) {
            Location lastKnownLocation = new Location("");
            lastKnownLocation.setLatitude(lastKnownLatitude);
            lastKnownLocation.setLongitude(lastKnownLongitude);
            return lastKnownLocation;
        } else {
            gpsTracker.showSettingsAlert();
            return null;
        }
    }
}
