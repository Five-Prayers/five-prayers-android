package com.hbouzidi.fiveprayers.ui.settings.method;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

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
