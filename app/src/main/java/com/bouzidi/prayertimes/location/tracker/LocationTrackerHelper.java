package com.bouzidi.prayertimes.location.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import static android.content.Context.MODE_PRIVATE;

public class LocationTrackerHelper {

    public static Location getLocation(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("location", MODE_PRIVATE);

        final double latitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_latitude", 0);
        final double longitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_longitude", 0);

        FallbackLocationTracker tracker =
                new FallbackLocationTracker(context);
        tracker.start((oldLoc, oldTime, newLoc, newTime) -> {
            if (latitude != 0 && longitude != 0) {
                Location storedLocation = new Location("");
                storedLocation.setLatitude(latitude);
                storedLocation.setLongitude(longitude);

                float distance = newLoc.distanceTo(storedLocation);

                if (distance > 1000 * 20) { // 20km
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_location_obsolete", true);
                    editor.apply();
                }
            }
        });

        Location location = tracker.getPossiblyStaleLocation();

        if (location != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            UserPreferencesUtils.putDouble(editor, "last_known_latitude", location.getLatitude());
            UserPreferencesUtils.putDouble(editor, "last_known_longitude", location.getLongitude());

            editor.putBoolean("is_location_obsolete", false);
            editor.apply();
        }
        return location;
    }
}
