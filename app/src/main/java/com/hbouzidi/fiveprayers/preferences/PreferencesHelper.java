package com.hbouzidi.fiveprayers.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbouzidi.fiveprayers.quran.dto.QuranBookmark;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.CountryCalculationMethod;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.TimingsTuneEnum;
import com.hbouzidi.fiveprayers.utils.UserPreferencesUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesHelper {

    public static void setFirstTimeLaunch(boolean isFirstTime, Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(PreferencesConstants.FIRST_LAUNCH, isFirstTime);

        edit.apply();
    }

    public static boolean isFirstLaunch(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        return sharedPreferences.getBoolean(PreferencesConstants.FIRST_LAUNCH, true);
    }

    public static CalculationMethodEnum getCalculationMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timingsCalculationMethodId = defaultSharedPreferences.getString(PreferencesConstants.TIMINGS_CALCULATION_METHOD_PREFERENCE, String.valueOf(CalculationMethodEnum.getDefault()));

        return CalculationMethodEnum.valueOf(timingsCalculationMethodId);
    }

    public static String getTune(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);

        int fajrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.FAJR_TIMING_ADJUSTMENT, 0);
        int dohrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.DOHR_TIMING_ADJUSTMENT, 0);
        int asrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.ASR_TIMING_ADJUSTMENT, 0);
        int maghrebTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.MAGHREB_TIMING_ADJUSTMENT, 0);
        int ichaTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.ICHA_TIMING_ADJUSTMENT, 0);

        return fajrTimingAdjustment + "," + fajrTimingAdjustment + ",0," + dohrTimingAdjustment + "," + asrTimingAdjustment + "," + maghrebTimingAdjustment + ",0," + ichaTimingAdjustment + ",0";
    }

    public static int getHijriAdjustment(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return defaultSharedPreferences.getInt(PreferencesConstants.HIJRI_DAY_ADJUSTMENT_PREFERENCE, 0);
    }

    public static LatitudeAdjustmentMethod getLatitudeAdjustmentMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String latitudeAdjustmentMethod = defaultSharedPreferences.getString(PreferencesConstants.TIMINGS_LATITUDE_ADJUSTMENT_METHOD_PREFERENCE, LatitudeAdjustmentMethod.getDefault().toString());

        return LatitudeAdjustmentMethod.valueOf(latitudeAdjustmentMethod);
    }

    public static SchoolAdjustmentMethod getSchoolAdjustmentMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String schoolAdjustmentMethod = defaultSharedPreferences.getString(PreferencesConstants.SCHOOL_ADJUSTMENT_METHOD_PREFERENCE, SchoolAdjustmentMethod.getDefault().toString());

        return SchoolAdjustmentMethod.valueOf(schoolAdjustmentMethod);
    }

    public static MidnightModeAdjustmentMethod getMidnightModeAdjustmentMethod(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String midnightModeAdjustmentMethod = defaultSharedPreferences.getString(PreferencesConstants.MIDNIGHT_MODE_ADJUSTMENT_METHOD_PREFERENCE, MidnightModeAdjustmentMethod.getDefault().toString());

        return MidnightModeAdjustmentMethod.valueOf(midnightModeAdjustmentMethod);
    }

    @NonNull
    public static Address getLastKnownAddress(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        final String locality = sharedPreferences.getString(PreferencesConstants.LAST_KNOWN_LOCALITY, null);
        final String country = sharedPreferences.getString(PreferencesConstants.LAST_KNOWN_COUNTRY, null);
        final double latitude = UserPreferencesUtils.getDouble(sharedPreferences, PreferencesConstants.LAST_KNOWN_LATITUDE, 0);
        final double longitude = UserPreferencesUtils.getDouble(sharedPreferences, PreferencesConstants.LAST_KNOWN_LONGITUDE, 0);

        Address address = new Address(Locale.getDefault());
        address.setCountryName(country);
        address.setLocality(locality);
        address.setLatitude(latitude);
        address.setLongitude(longitude);

        return address;
    }

    public static void updateTimingAdjustmentPreference(String methodName, Context context) {
        if (!isCalculationPreferenceInitialized(context)) {
            TimingsTuneEnum timingsTuneEnum = TimingsTuneEnum.getValueByName(methodName);

            SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(PreferencesConstants.FAJR_TIMING_ADJUSTMENT, timingsTuneEnum.getFajr());
            editor.putInt(PreferencesConstants.DOHR_TIMING_ADJUSTMENT, timingsTuneEnum.getDhuhr());
            editor.putInt(PreferencesConstants.ASR_TIMING_ADJUSTMENT, timingsTuneEnum.getAsr());
            editor.putInt(PreferencesConstants.MAGHREB_TIMING_ADJUSTMENT, timingsTuneEnum.getMaghrib());
            editor.putInt(PreferencesConstants.ICHA_TIMING_ADJUSTMENT, timingsTuneEnum.getIsha());

            editor.apply();
        }
    }

    public static void updateAddressPreferences(Context context, Address address) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PreferencesConstants.LAST_KNOWN_LOCALITY, address.getLocality());
        editor.putString(PreferencesConstants.LAST_KNOWN_COUNTRY, address.getCountryName());
        editor.putString(PreferencesConstants.LAST_KNOWN_COUNTRY_CODE, address.getCountryCode());
        editor.putString(PreferencesConstants.LAST_KNOWN_STATE, address.getAddressLine(1));

        UserPreferencesUtils.putDouble(editor, PreferencesConstants.LAST_KNOWN_LATITUDE, address.getLatitude());
        UserPreferencesUtils.putDouble(editor, PreferencesConstants.LAST_KNOWN_LONGITUDE, address.getLongitude());
        editor.apply();

        CalculationMethodEnum calculationMethodByAddress = CountryCalculationMethod.getCalculationMethodByAddress(address);

        PreferencesHelper.updateCalculationMethodPreferenceByAddress(String.valueOf(calculationMethodByAddress), context);
        PreferencesHelper.updateTimingAdjustmentPreference(String.valueOf(calculationMethodByAddress), context);

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor defaultEditor = defaultSharedPreferences.edit();
        defaultEditor.putString(PreferencesConstants.LOCATION_PREFERENCE, address.getLocality() + ", " + address.getCountryName());
        defaultEditor.putBoolean(PreferencesConstants.CALCULATION_PREFERENCES_INITIALIZED, true);
        defaultEditor.apply();
    }

    public static boolean isLocationSetManually(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.LOCATION_SET_MANUALLY_PREFERENCE, false);
    }

    public static void setNightModeActivated(Context context, boolean activated) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putBoolean(PreferencesConstants.QURAN_NIGHT_MODE_ACTIVATED, activated);
        edit.apply();
    }

    public static boolean isNightModeActivated(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.QURAN_NIGHT_MODE_ACTIVATED, false);
    }

    public static Boolean isVibrationActivated(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.ADHAN_VIBRATION_PREFERENCE, true);
    }

    public static String getFajrAdhanCaller(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(PreferencesConstants.ADTHAN_FAJR_CALLER, "SHORT_PRAYER_CALL");
    }

    public static String getAdhanCaller(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(PreferencesConstants.ADTHAN_CALLER, "SHORT_PRAYER_CALL");
    }

    public static void saveAutomaticBookmarkList(List<QuranBookmark> automaticBookmarks, Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();

        Gson gson = new Gson();
        String list = gson.toJson(automaticBookmarks);

        edit.putString(PreferencesConstants.AUTOMATIC_BOOKMARK_LIST, list);
        edit.apply();
    }

    public static List<QuranBookmark> getSortedAutomaticBookmarkList(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String listStr = defaultSharedPreferences.getString(PreferencesConstants.AUTOMATIC_BOOKMARK_LIST, null);

        if (listStr != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<QuranBookmark>>() {
            }.getType();

            List<QuranBookmark> list = gson.fromJson(listStr, type);

            return list
                    .stream()
                    .sorted(Comparator.comparing(QuranBookmark::getTimestamps).reversed())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return new ArrayList<>();
    }

    private static boolean isCalculationPreferenceInitialized(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.CALCULATION_PREFERENCES_INITIALIZED, false);
    }

    private static void updateCalculationMethodPreferenceByAddress(String methodName, Context context) {
        if (!isCalculationPreferenceInitialized(context)) {
            final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor defaultEditor = defaultSharedPreferences.edit();
            defaultEditor.putString(PreferencesConstants.TIMINGS_CALCULATION_METHOD_PREFERENCE, methodName);
            defaultEditor.apply();
        }
    }
}
