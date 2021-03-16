package com.hbouzidi.fiveprayers.ui.settings.method;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class CalculationMethodPreference extends ListPreference {

    public CalculationMethodPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        OnPreferenceChangeListener onPreferenceChangeListener = (preference, newValue) -> {
            PreferencesHelper.updateTimingAdjustmentPreference(String.valueOf(newValue), getContext());
            return true;
        };

        setOnPreferenceChangeListener(onPreferenceChangeListener);
    }
}
