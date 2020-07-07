package com.bouzidi.prayertimes.ui.settings.method;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.bouzidi.prayertimes.preferences.PreferencesHelper;

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
