package com.bouzidi.prayertimes.location.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import io.reactivex.rxjava3.core.Single;

import static android.content.Context.MODE_PRIVATE;

public class LocationHelper {

    public static Single<Location> getLocation(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("location", MODE_PRIVATE);

        final double lastKnownLatitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_latitude", 0);
        final double lastKnownLongitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_longitude", 0);

        GPSTracker gpsTracker = new GPSTracker(context);

        return Single.create(emitter -> {
            if (gpsTracker.canGetLocation()) {
                Location newLocation = gpsTracker.getLocation();

                if (newLocation != null) {
                    double newLatitude = newLocation.getLatitude();
                    double newLongitude = newLocation.getLongitude();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    UserPreferencesUtils.putDouble(editor, "last_known_latitude", newLatitude);
                    UserPreferencesUtils.putDouble(editor, "last_known_longitude", newLongitude);

                    editor.apply();

                    emitter.onSuccess(newLocation);
                } else {
                    emitter.onError(new Exception("Unable to get location from providers"));
                }
            } else if (lastKnownLatitude >= 0 || lastKnownLongitude >= 0) {
                Location lastKnownLocation = new Location("");
                lastKnownLocation.setLatitude(lastKnownLatitude);
                lastKnownLocation.setLongitude(lastKnownLongitude);
                emitter.onSuccess(lastKnownLocation);
            } else {
                gpsTracker.showSettingsAlert();
                emitter.onError(new Exception("Unable to find location"));
            }
        });
    }
}
