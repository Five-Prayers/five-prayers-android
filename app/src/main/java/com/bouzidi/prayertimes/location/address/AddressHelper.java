package com.bouzidi.prayertimes.location.address;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.bouzidi.prayertimes.exceptions.LocationException;
import com.bouzidi.prayertimes.location.osm.NominatimAPIService;
import com.bouzidi.prayertimes.location.osm.NominatimReverseGeocodeResponse;
import com.bouzidi.prayertimes.network.NetworkUtil;
import com.bouzidi.prayertimes.preferences.PreferencesHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Single;

public class AddressHelper {

    private static final int MINIMUM_DISTANCE_FOR_OBSOLESCENCE = 1000; //1KM

    public static Single<Address> getAddressFromLocation(final Location location,
                                                         final Context context) {

        return Single.create(emitter -> {
            if (PreferencesHelper.isLocationSetManually(context)) {
                Address lastKnownAddress = PreferencesHelper.getLastKnownAddress(context);
                emitter.onSuccess(lastKnownAddress);
            } else if (location == null) {
                Log.e(AddressHelper.class.getName(), "Location is null");
                emitter.onError(new LocationException("Cannot get Address from null Location"));
            } else {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Address lastKnownAddress = PreferencesHelper.getLastKnownAddress(context);

                if (!isAddressObsolete(lastKnownAddress, latitude, longitude)) {
                    emitter.onSuccess(lastKnownAddress);
                } else {
                    Thread thread = new Thread(() -> {
                        try {
                            Address geocoderAddresses = getGeocoderAddresses(latitude, longitude, context);
                            if (geocoderAddresses != null) {
                                emitter.onSuccess(geocoderAddresses);
                            } else if (getNominatimAddress(latitude, longitude, context) != null) {
                                emitter.onSuccess(getNominatimAddress(latitude, longitude, context));
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
            PreferencesHelper.updateAddressPreferences(context, address);
            return address;
        }
        return null;
    }

    private static Address getNominatimAddress(double latitude, double longitude, Context context) throws IOException {
        NominatimAPIService nominatimAPIService = NominatimAPIService.getInstance();

        if (NetworkUtil.isNetworkAvailable(context)) {
            NominatimReverseGeocodeResponse response = nominatimAPIService.getAddressFromLocation(latitude, longitude);

            Address address = new Address(Locale.getDefault());
            address.setCountryName(response.getAddress().getCountry());
            address.setCountryCode(response.getAddress().getCountryCode());
            address.setAddressLine(1, response.getAddress().getState());
            address.setLocality(response.getAddress().getLocality());
            address.setPostalCode(response.getAddress().getPostcode());
            address.setLatitude(response.getLat());
            address.setLongitude(response.getLon());

            PreferencesHelper.updateAddressPreferences(context, address);

            return address;
        }
        return null;
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
}
