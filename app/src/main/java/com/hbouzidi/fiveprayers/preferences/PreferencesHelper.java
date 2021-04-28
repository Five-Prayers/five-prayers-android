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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class PreferencesHelper {

    private final Context context;

    @Inject
    public PreferencesHelper(Context context) {
        this.context = context;
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(PreferencesConstants.FIRST_LAUNCH, isFirstTime);

        edit.apply();
    }

    public boolean isFirstLaunch() {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        return sharedPreferences.getBoolean(PreferencesConstants.FIRST_LAUNCH, true);
    }

    public CalculationMethodEnum getCalculationMethod() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timingsCalculationMethodId = defaultSharedPreferences.getString(PreferencesConstants.TIMINGS_CALCULATION_METHOD_PREFERENCE, String.valueOf(CalculationMethodEnum.getDefault()));

        return CalculationMethodEnum.valueOf(timingsCalculationMethodId);
    }

    public String getTune() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);

        int fajrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.FAJR_TIMING_ADJUSTMENT, 0);
        int dohrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.DOHR_TIMING_ADJUSTMENT, 0);
        int asrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.ASR_TIMING_ADJUSTMENT, 0);
        int maghrebTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.MAGHREB_TIMING_ADJUSTMENT, 0);
        int ichaTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.ICHA_TIMING_ADJUSTMENT, 0);

        return fajrTimingAdjustment + "," + fajrTimingAdjustment + ",0," + dohrTimingAdjustment + "," + asrTimingAdjustment + "," + maghrebTimingAdjustment + ",0," + ichaTimingAdjustment + ",0";
    }

    public Map<String, Integer> getTuneMap() {
        HashMap<String, Integer> map = new HashMap<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);

        int fajrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.FAJR_TIMING_ADJUSTMENT, 0);
        int dohrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.DOHR_TIMING_ADJUSTMENT, 0);
        int asrTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.ASR_TIMING_ADJUSTMENT, 0);
        int maghrebTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.MAGHREB_TIMING_ADJUSTMENT, 0);
        int ichaTimingAdjustment = sharedPreferences.getInt(PreferencesConstants.ICHA_TIMING_ADJUSTMENT, 0);

        map.put(PreferencesConstants.FAJR_TIMING_ADJUSTMENT, fajrTimingAdjustment);
        map.put(PreferencesConstants.DOHR_TIMING_ADJUSTMENT, dohrTimingAdjustment);
        map.put(PreferencesConstants.ASR_TIMING_ADJUSTMENT, asrTimingAdjustment);
        map.put(PreferencesConstants.MAGHREB_TIMING_ADJUSTMENT, maghrebTimingAdjustment);
        map.put(PreferencesConstants.ICHA_TIMING_ADJUSTMENT, ichaTimingAdjustment);

        return map;
    }

    public int getHijriAdjustment() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return defaultSharedPreferences.getInt(PreferencesConstants.HIJRI_DAY_ADJUSTMENT_PREFERENCE, 0);
    }

    public LatitudeAdjustmentMethod getLatitudeAdjustmentMethod() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String latitudeAdjustmentMethod = defaultSharedPreferences.getString(PreferencesConstants.TIMINGS_LATITUDE_ADJUSTMENT_METHOD_PREFERENCE, LatitudeAdjustmentMethod.getDefault().toString());

        return LatitudeAdjustmentMethod.valueOf(latitudeAdjustmentMethod);
    }

    public SchoolAdjustmentMethod getSchoolAdjustmentMethod() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String schoolAdjustmentMethod = defaultSharedPreferences.getString(PreferencesConstants.SCHOOL_ADJUSTMENT_METHOD_PREFERENCE, SchoolAdjustmentMethod.getDefault().toString());

        return SchoolAdjustmentMethod.valueOf(schoolAdjustmentMethod);
    }

    public MidnightModeAdjustmentMethod getMidnightModeAdjustmentMethod() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String midnightModeAdjustmentMethod = defaultSharedPreferences.getString(PreferencesConstants.MIDNIGHT_MODE_ADJUSTMENT_METHOD_PREFERENCE, MidnightModeAdjustmentMethod.getDefault().toString());

        return MidnightModeAdjustmentMethod.valueOf(midnightModeAdjustmentMethod);
    }

    @NonNull
    public Address getLastKnownAddress() {
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

    public void updateTimingAdjustmentPreference(String methodName) {
        if (!isCalculationPreferenceInitialized()) {
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

    public void updateAddressPreferences(Address address) {
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

        updateCalculationMethodPreferenceByAddress(String.valueOf(calculationMethodByAddress));
        updateTimingAdjustmentPreference(String.valueOf(calculationMethodByAddress));

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor defaultEditor = defaultSharedPreferences.edit();

        if (address.getLocality() != null && address.getCountryName() != null) {
            defaultEditor.putString(PreferencesConstants.LOCATION_PREFERENCE, address.getLocality() + ", " + address.getCountryName());
        } else {
            defaultEditor.putString(PreferencesConstants.LOCATION_PREFERENCE, address.getLatitude() + ", " + address.getLongitude());
        }

        defaultEditor.putBoolean(PreferencesConstants.CALCULATION_PREFERENCES_INITIALIZED, true);
        defaultEditor.apply();
    }

    public boolean isLocationSetManually() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.LOCATION_SET_MANUALLY_PREFERENCE, false);
    }

    public void setNightModeActivated(boolean activated) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putBoolean(PreferencesConstants.QURAN_NIGHT_MODE_ACTIVATED, activated);
        edit.apply();
    }

    public boolean isNightModeActivated() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.QURAN_NIGHT_MODE_ACTIVATED, false);
    }

    public Boolean isVibrationActivated() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.ADHAN_VIBRATION_PREFERENCE, true);
    }

    public String getFajrAdhanCaller() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(PreferencesConstants.ADTHAN_FAJR_CALLER, "SHORT_PRAYER_CALL");
    }

    public String getAdhanCaller() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(PreferencesConstants.ADTHAN_CALLER, "SHORT_PRAYER_CALL");
    }

    public boolean isDouaeAfterAdhanEnabled() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.DOUAE_AFTER_ADHAN_PREFERENCE, true);
    }

    public boolean isDohaReminderEnabled() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.DOHA_TIMING_REMINDER_ENABLED, false);
    }

    public boolean isReminderEnabled() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.ADTHAN_REMINDER_ENABLED, true);
    }

    public int getReminderInterval() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getInt(PreferencesConstants.ADTHAN_REMINDER_INTERVAL, 10);
    }

    public void saveAutomaticBookmarkList(List<QuranBookmark> automaticBookmarks) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();

        Gson gson = new Gson();
        String list = gson.toJson(automaticBookmarks);

        edit.putString(PreferencesConstants.AUTOMATIC_BOOKMARK_LIST, list);
        edit.apply();
    }

    public List<QuranBookmark> getSortedAutomaticBookmarkList() {
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

    private boolean isCalculationPreferenceInitialized() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(PreferencesConstants.CALCULATION_PREFERENCES_INITIALIZED, false);
    }

    private void updateCalculationMethodPreferenceByAddress(String methodName) {
        if (!isCalculationPreferenceInitialized()) {
            final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor defaultEditor = defaultSharedPreferences.edit();
            defaultEditor.putString(PreferencesConstants.TIMINGS_CALCULATION_METHOD_PREFERENCE, methodName);
            defaultEditor.apply();
        }
    }
}
