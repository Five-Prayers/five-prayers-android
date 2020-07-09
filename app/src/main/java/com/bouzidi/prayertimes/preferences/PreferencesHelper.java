package com.bouzidi.prayertimes.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import androidx.preference.PreferenceManager;

import com.bouzidi.prayertimes.timings.calculations.CalculationMethodEnum;
import com.bouzidi.prayertimes.timings.calculations.CountryCalculationMethod;
import com.bouzidi.prayertimes.timings.calculations.LatitudeAdjustmentMethod;
import com.bouzidi.prayertimes.timings.calculations.MidnightModeAdjustmentMethod;
import com.bouzidi.prayertimes.timings.calculations.SchoolAdjustmentMethod;
import com.bouzidi.prayertimes.timings.calculations.TimingsTuneEnum;
import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesHelper {

    public static void setFirstTimeLaunch(boolean isFirstTime, Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(Constants.FIRST_LAUNCH, isFirstTime);

        edit.apply();
    }

    public static boolean isFirstLaunch(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCATION, MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.FIRST_LAUNCH, true);
    }

    public static CalculationMethodEnum getCalculationMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timingsCalculationMethodId = defaultSharedPreferences.getString(Constants.TIMINGS_CALCULATION_METHOD_PREFERENCE, String.valueOf(CalculationMethodEnum.getDefault()));

        return CalculationMethodEnum.valueOf(timingsCalculationMethodId);
    }

    public static String getTune(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);

        int fajrTimingAdjustment = sharedPreferences.getInt(Constants.FAJR_TIMING_ADJUSTMENT, 0);
        int dohrTimingAdjustment = sharedPreferences.getInt(Constants.DOHR_TIMING_ADJUSTMENT, 0);
        int asrTimingAdjustment = sharedPreferences.getInt(Constants.ASR_TIMING_ADJUSTMENT, 0);
        int maghrebTimingAdjustment = sharedPreferences.getInt(Constants.MAGHREB_TIMING_ADJUSTMENT, 0);
        int ichaTimingAdjustment = sharedPreferences.getInt(Constants.ICHA_TIMING_ADJUSTMENT, 0);

        return fajrTimingAdjustment + "," + fajrTimingAdjustment + ",0," + dohrTimingAdjustment + "," + asrTimingAdjustment + "," + maghrebTimingAdjustment + ",0," + ichaTimingAdjustment + ",0";
    }

    public static int getHijriAdjustment(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return defaultSharedPreferences.getInt(Constants.HIJRI_DAY_ADJUSTMENT_PREFERENCE, 0);
    }

    public static LatitudeAdjustmentMethod getLatitudeAdjustmentMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String latitudeAdjustmentMethod = defaultSharedPreferences.getString(Constants.TIMINGS_LATITUDE_ADJUSTMENT_METHOD_PREFERENCE, LatitudeAdjustmentMethod.getDefault().toString());

        return LatitudeAdjustmentMethod.valueOf(latitudeAdjustmentMethod);
    }

    public static SchoolAdjustmentMethod getSchoolAdjustmentMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String schoolAdjustmentMethod = defaultSharedPreferences.getString(Constants.SCHOOL_ADJUSTMENT_METHOD_PREFERENCE, SchoolAdjustmentMethod.getDefault().toString());

        return SchoolAdjustmentMethod.valueOf(schoolAdjustmentMethod);
    }

    public static MidnightModeAdjustmentMethod getMidnightModeAdjustmentMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String midnightModeAdjustmentMethod = defaultSharedPreferences.getString(Constants.MIDNIGHT_MODE_ADJUSTMENT_METHOD_PREFERENCE, MidnightModeAdjustmentMethod.getDefault().toString());

        return MidnightModeAdjustmentMethod.valueOf(midnightModeAdjustmentMethod);
    }

    public static void updateCalculationMethodPreferenceByAddress(String methodName, Context context) {
        if (!isCalculationSetManually(context)) {
            final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            defaultSharedPreferences.getBoolean(Constants.CALCULATION_SET_MANUALLY_PREFERENCE, false);
            SharedPreferences.Editor defaultEditor = defaultSharedPreferences.edit();
            defaultEditor.putString(Constants.TIMINGS_CALCULATION_METHOD_PREFERENCE, methodName);
            defaultEditor.apply();
        }
    }

    @NotNull
    public static Address getLastKnownAddress(Context context) {
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

    public static void updateTimingAdjustmentPreference(String methodName, Context context) {
        if (!isCalculationSetManually(context)) {
            TimingsTuneEnum timingsTuneEnum = TimingsTuneEnum.getValueByName(methodName);

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(Constants.FAJR_TIMING_ADJUSTMENT, timingsTuneEnum.getFajr());
            editor.putInt(Constants.DOHR_TIMING_ADJUSTMENT, timingsTuneEnum.getDhuhr());
            editor.putInt(Constants.ASR_TIMING_ADJUSTMENT, timingsTuneEnum.getAsr());
            editor.putInt(Constants.MAGHREB_TIMING_ADJUSTMENT, timingsTuneEnum.getMaghrib());
            editor.putInt(Constants.ICHA_TIMING_ADJUSTMENT, timingsTuneEnum.getIsha());

            editor.apply();
        }
    }

    public static void updateAddressPreferences(Context context, Address address) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.LAST_KNOWN_LOCALITY, address.getLocality());
        editor.putString(Constants.LAST_KNOWN_COUNTRY, address.getCountryName());
        editor.putString(Constants.LAST_KNOWN_COUNTRY_CODE, address.getCountryCode());
        editor.putString(Constants.LAST_KNOWN_STATE, address.getAddressLine(1));

        UserPreferencesUtils.putDouble(editor, Constants.LAST_KNOWN_LATITUDE, address.getLatitude());
        UserPreferencesUtils.putDouble(editor, Constants.LAST_KNOWN_LONGITUDE, address.getLongitude());
        editor.apply();

        CalculationMethodEnum calculationMethodByAddress = CountryCalculationMethod.getCalculationMethodByAddress(address);

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor defaultEditor = defaultSharedPreferences.edit();
        defaultEditor.putString(Constants.LOCATION_PREFERENCE, address.getLocality() + ", " + address.getCountryName());
        defaultEditor.apply();

        PreferencesHelper.updateCalculationMethodPreferenceByAddress(String.valueOf(calculationMethodByAddress), context);
        PreferencesHelper.updateTimingAdjustmentPreference(String.valueOf(calculationMethodByAddress), context);
    }

    public static boolean isCalculationSetManually(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(Constants.CALCULATION_SET_MANUALLY_PREFERENCE, false);
    }

    public static boolean isLocationSetManually(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(Constants.LOCATION_SET_MANUALLY_PREFERENCE, false);
    }
}
