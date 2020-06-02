package com.bouzidi.prayer_times.location.tracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class FallbackLocationTracker implements LocationTracker, LocationTracker.LocationUpdateListener {

    private boolean isRunning;

    private ProviderLocationTracker gps;
    private ProviderLocationTracker net;

    private LocationUpdateListener listener;

    Location lastLoc;
    long lastTime;

    public FallbackLocationTracker(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.GPS);
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            net = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.NETWORK);
        }
    }

    public void start() {
        if (isRunning) {
            //Already running, do nothing
            return;
        }

        //Start both
        if (gps != null) {
            gps.start(this);
            isRunning = true;
        }
        if (net != null) {
            net.start(this);
            isRunning = true;
        }
    }

    public void start(LocationUpdateListener update) {
        start();
        listener = update;
    }


    public void stop() {
        if (isRunning) {
            if (gps != null) {
                gps.stop();
            }
            if (net != null) {
                net.stop();
            }
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation() {
        //If either has a location, use it
        return (gps != null && gps.hasLocation()) || (net != null && net.hasLocation());
    }

    public boolean hasPossiblyStaleLocation() {
        //If either has a location, use it
        return (gps != null && gps.hasPossiblyStaleLocation()) || (net != null && net.hasPossiblyStaleLocation());
    }

    public Location getLocation() {
        Location ret = null;

        if (gps != null) {
            ret = gps.getLocation();
        }
        if (ret == null && net != null) {
            ret = net.getLocation();
        }
        return ret;
    }

    public Location getPossiblyStaleLocation() {
        Location ret = null;

        if (gps != null) {
            ret = gps.getPossiblyStaleLocation();
        }
        if (ret == null && net != null) {
            ret = net.getPossiblyStaleLocation();
        }
        return ret;
    }

    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
        boolean update = false;

        //We should update only if there is no last location, the provider is the same, or the provider is more accurate, or the old location is stale
        if (lastLoc == null) {
            update = true;
        } else if (lastLoc.getProvider().equals(newLoc.getProvider())) {
            update = true;
        } else if (newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            update = true;
        } else if (newTime - lastTime > 5 * 60 * 1000) {
            update = true;
        }

        if (update) {
            if (listener != null) {
                listener.onUpdate(lastLoc, lastTime, newLoc, newTime);
            }
            lastLoc = newLoc;
            lastTime = newTime;
        }
    }
}
