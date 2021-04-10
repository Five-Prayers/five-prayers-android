package com.hbouzidi.fiveprayers.ui.settings.method;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class CalculationMethodPreference extends ListPreference {

    @Inject
    PreferencesHelper preferencesHelper;

    public CalculationMethodPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        ((FivePrayerApplication) getContext()
                .getApplicationContext())
                .appComponent
                .settingsComponent()
                .create()
                .inject(this);

        OnPreferenceChangeListener onPreferenceChangeListener = (preference, newValue) -> {
            preferencesHelper.updateTimingAdjustmentPreference(String.valueOf(newValue));
            return true;
        };

        setOnPreferenceChangeListener(onPreferenceChangeListener);
    }
}
