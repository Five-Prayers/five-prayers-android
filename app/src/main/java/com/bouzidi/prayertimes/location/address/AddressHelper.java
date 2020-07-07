package com.bouzidi.prayertimes.location.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.bouzidi.prayertimes.exceptions.LocationException;
import com.bouzidi.prayertimes.location.osm.NominatimAPIService;
import com.bouzidi.prayertimes.location.osm.NominatimReverseGeocodeResponse;
import com.bouzidi.prayertimes.network.NetworkUtil;
import com.bouzidi.prayertimes.timings.calculations.CalculationMethodEnum;
import com.bouzidi.prayertimes.timings.calculations.CalculationMethodHelper;
import com.bouzidi.prayertimes.timings.calculations.CountryCalculationMethodHelper;
import com.bouzidi.prayertimes.utils.Constants;
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

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean locationSetManually = defaultSharedPreferences.getBoolean(Constants.LOCATION_SET_MANUALLY_PREFERENCE, false);

        return Single.create(emitter -> {
            if (locationSetManually) {
                Address lastKnownAddress = getLastKnownAddress(context);
                emitter.onSuccess(lastKnownAddress);
            } else if (location == null) {
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

    public static void updateUserPreferences(Context context, Address address) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.LAST_KNOWN_LOCALITY, address.getLocality());
        editor.putString(Constants.LAST_KNOWN_COUNTRY, address.getCountryName());
        editor.putString(Constants.LAST_KNOWN_COUNTRY_CODE, address.getCountryCode());
        editor.putString(Constants.LAST_KNOWN_STATE, address.getAddressLine(1));

        UserPreferencesUtils.putDouble(editor, Constants.LAST_KNOWN_LATITUDE, address.getLatitude());
        UserPreferencesUtils.putDouble(editor, Constants.LAST_KNOWN_LONGITUDE, address.getLongitude());
        editor.apply();

        CalculationMethodEnum calculationMethodByAddress = CountryCalculationMethodHelper.getCalculationMethodByAddress(address);

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor defaultEditor = defaultSharedPreferences.edit();
        defaultEditor.putString(Constants.LOCATION_PREFERENCE, address.getLocality() + ", " + address.getCountryName());
        defaultEditor.putString(Constants.TIMINGS_CALCULATION_METHOD_PREFERENCE, calculationMethodByAddress.toString());
        defaultEditor.apply();

        CalculationMethodHelper.updateTimingAdjustmentPreference(calculationMethodByAddress.toString(), context);
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

    private static Address getNominatimAddress(double latitude, double longitude, Context context) throws IOException {
        NominatimAPIService nominatimAPIService = NominatimAPIService.getInstance();

        if (NetworkUtil.isNetworkAvailable(context)) {
            NominatimReverseGeocodeResponse response = nominatimAPIService.getAddressFromLocation(latitude, longitude);

            Address address = new Address(Locale.getDefault());
            address.setCountryName(response.getAddress().getCountry());
            address.setCountryCode(response.getAddress().getCountryCode());
            address.setAddressLine(1, response.getAddress().getState());
            address.setLocality(response.getAddress().getTown());
            address.setPostalCode(response.getAddress().getPostcode());
            address.setLatitude(response.getLat());
            address.setLongitude(response.getLon());

            updateUserPreferences(context, address);

            return address;
        }
        return null;
    }

    @NotNull
    private static Address getLastKnownAddress(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCATION, MODE_PRIVATE);
        final String locality = sharedPreferences.getString(Constants.LAST_KNOWN_LOCALITY, null);
        final String country = sharedPreferences.getString(Constants.LAST_KNOWN_COUNTRY, null);
        final double latitude = UserPreferencesUtils.getDouble(sharedPreferences, Constants.LAST_KNOWN_LATITUDE, 0);
        final double longitude = UserPreferencesUtils.getDouble(sharedPreferences, Constants.LAST_KNOWN_LONGITUDE, 0);

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
}
