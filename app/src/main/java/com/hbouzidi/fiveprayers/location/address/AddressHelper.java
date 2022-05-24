package com.hbouzidi.fiveprayers.location.address;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.exceptions.LocationException;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIService;
import com.hbouzidi.fiveprayers.location.osm.NominatimReverseGeocodeResponse;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class AddressHelper {

    private static final int MINIMUM_DISTANCE_FOR_OBSOLESCENCE = 1000; //1KM

    private final Context context;
    private final NominatimAPIService nominatimAPIService;
    private final PreferencesHelper preferencesHelper;

    @Inject
    public AddressHelper(Context context, NominatimAPIService nominatimAPIService, PreferencesHelper preferencesHelper) {
        this.context = context;
        this.nominatimAPIService = nominatimAPIService;
        this.preferencesHelper = preferencesHelper;
    }

    public Single<Address> getAddressFromLocation(final Location location) {

        return Single.create(emitter -> {
            boolean locationSetManually = preferencesHelper.isLocationSetManually();
            if (locationSetManually) {
                Address lastKnownAddress = preferencesHelper.getLastKnownAddress();
                emitter.onSuccess(lastKnownAddress);
            } else if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Address lastKnownAddress = preferencesHelper.getLastKnownAddress();

                if (!isAddressObsolete(lastKnownAddress, latitude, longitude)) {
                    emitter.onSuccess(lastKnownAddress);
                } else {
                    Thread thread = new Thread(() -> {

                        Address geocoderAddresses = getGeocoderAddresses(latitude, longitude, context);
                        if (geocoderAddresses != null) {
                            emitter.onSuccess(geocoderAddresses);
                        } else if (getNominatimAddress(latitude, longitude) != null) {
                            emitter.onSuccess(getNominatimAddress(latitude, longitude));
                        } else {
                            Log.i(AddressHelper.class.getName(), "Offline address");
                            emitter.onSuccess(getOfflineAddress(latitude, longitude));
                        }
                    });
                    thread.start();
                }
            } else {
                Log.e(AddressHelper.class.getName(), "Location is null");
                emitter.onError(new LocationException(context.getResources().getString(R.string.location_service_unavailable)));
            }
        });
    }

    private Address getGeocoderAddresses(double latitude, double longitude, Context context) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

                if (address.getCountryName() != null && address.getLocality() != null) {
                    preferencesHelper.updateAddressPreferences(address);
                    return address;
                }
                return null;
            }
            return null;
        } catch (IOException e) {
            Log.e("ADDRESS_HELPER", "Cannot get address from Geocoder", e);
            return null;
        }
    }

    private Address getNominatimAddress(double latitude, double longitude) {
        try {
            NominatimReverseGeocodeResponse response = nominatimAPIService.getAddressFromLocation(latitude, longitude);

            if (response != null && response.getAddress() != null && response.getAddress().getCountry() != null && response.getAddress().getLocality() != null) {
                Address address = new Address(Locale.getDefault());
                address.setCountryName(response.getAddress().getCountry());
                address.setCountryCode(response.getAddress().getCountryCode());
                address.setAddressLine(1, response.getAddress().getState());
                address.setLocality(response.getAddress().getLocality());
                address.setPostalCode(response.getAddress().getPostcode());
                address.setLatitude(response.getLat());
                address.setLongitude(response.getLon());

                preferencesHelper.updateAddressPreferences(address);

                return address;
            }

            return null;
        } catch (IOException e) {
            Log.e("ADDRESS_HELPER", "Cannot get address from Nominatim", e);
            return null;
        }
    }

    private Address getOfflineAddress(double latitude, double longitude) {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(latitude);
        address.setLongitude(longitude);

        preferencesHelper.updateAddressPreferences(address);

        return address;
    }

    private boolean isAddressObsolete(Address lastKnownAddress, double latitude, double longitude) {
        if (lastKnownAddress.getLocality() != null && lastKnownAddress.getCountryName() != null) {

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
