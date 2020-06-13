package com.bouzidi.prayertimes.location.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.bouzidi.prayertimes.exceptions.LocationException;
import com.bouzidi.prayertimes.location.arcgis.ArcgisAPIService;
import com.bouzidi.prayertimes.location.arcgis.ArcgisReverseGeocodeResponse;
import com.bouzidi.prayertimes.network.NetworkUtil;
import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Single;

import static android.content.Context.MODE_PRIVATE;

public class AddressHelper {

    private static final int MINIMUM_DISTANCE_FOR_OBSOLESCENCE = 1000; //1KM

    public static Single<Address> getAddressFromLocation(final Location location,
                                                         final Context context) {

        return Single.create(emitter -> {
            if (location == null) {
                Log.e(AddressHelper.class.getName(), "Location is null");
                emitter.onError(new LocationException("Cannot get Address from null Location"));
            } else {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Address lastKnownAddress = getLastKnownAddress(context);

                if (!isAddressObsolete(lastKnownAddress, latitude, longitude)) {
                    emitter.onSuccess(lastKnownAddress);
                } else {
                    Thread thread = new Thread(() -> {
                        try {
                            Address geocoderAddresses = getGeocoderAddresses(latitude, longitude, context);
                            if (geocoderAddresses != null) {
                                emitter.onSuccess(geocoderAddresses);
                            } else if (getArcgisAddress(latitude, longitude, context) != null) {
                                emitter.onSuccess(getArcgisAddress(latitude, longitude, context));
                            } else if (lastKnownAddress.getLocality() != null) {
                                emitter.onSuccess(lastKnownAddress);
                            } else {
                                Log.e(AddressHelper.class.getName(), "Unable connect to get address");
                                emitter.onError(new LocationException("Unable connect to get address"));
                            }
                        } catch (Exception e) {
                            Log.e(AddressHelper.class.getName(), "Unable connect to get address from API", e);
                            emitter.onError(new LocationException("Unable connect to get address from API", e));
                        }
                    });
                    thread.start();
                }
            }
        });
    }

    private static Address getGeocoderAddresses(double latitude, double longitude, Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

        if (addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);
            updateUserPreferences(context, address);
            return address;
        }
        return null;
    }

    private static Address getArcgisAddress(double latitude, double longitude, Context context) throws IOException {
        ArcgisAPIService arcgisReverseGeocodeService = ArcgisAPIService.getInstance();

        if (NetworkUtil.isNetworkAvailable(context)) {
            ArcgisReverseGeocodeResponse response = arcgisReverseGeocodeService.getAddressFromLocation(latitude, longitude);

            Address address = new Address(Locale.getDefault());
            address.setCountryName(response.getAddress().getCountryCode());
            address.setCountryCode(response.getAddress().getCountryCode());
            address.setLocality(response.getAddress().getCity());
            address.setPostalCode(response.getAddress().getPostal());
            address.setLatitude(response.getLocation().getX());
            address.setLongitude(response.getLocation().getY());

            updateUserPreferences(context, address);

            return address;
        }
        return null;
    }

    @NotNull
    private static Address getLastKnownAddress(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("location", MODE_PRIVATE);
        final String locality = sharedPreferences.getString("last_known_locality", null);
        final String country = sharedPreferences.getString("last_known_country", null);
        final double latitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_latitude", 0);
        final double longitude = UserPreferencesUtils.getDouble(sharedPreferences, "last_known_longitude", 0);

        Address address = new Address(Locale.getDefault());
        address.setCountryName(country);
        address.setLocality(locality);
        address.setLatitude(latitude);
        address.setLongitude(longitude);

        return address;
    }

    private static boolean isAddressObsolete(Address lastKnownAddress, double latitude, double longitude) {
        if (lastKnownAddress.getLocality() != null) {

            Location LastKnownLocation = new Location("");
            LastKnownLocation.setLatitude(lastKnownAddress.getLatitude());
            LastKnownLocation.setLongitude(lastKnownAddress.getLongitude());

            Location newLocation = new Location("");
            newLocation.setLatitude(latitude);
            newLocation.setLongitude(longitude);

            float distance = LastKnownLocation.distanceTo(newLocation);
            return distance > MINIMUM_DISTANCE_FOR_OBSOLESCENCE;
        }
        return true;
    }

    private static void updateUserPreferences(Context context, Address address) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("location", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_known_locality", address.getLocality());
        editor.putString("last_known_country", address.getCountryName());
        UserPreferencesUtils.putDouble(editor, "last_known_latitude", address.getLatitude());
        UserPreferencesUtils.putDouble(editor, "last_known_longitude", address.getLongitude());
        editor.apply();
    }
}
