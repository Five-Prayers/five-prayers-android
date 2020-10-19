package com.hbouzidi.fiveprayers.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.settings.hijri.HijriDayAdjustmentPreference;
import com.hbouzidi.fiveprayers.ui.settings.hijri.HijriDayAdjustmentPreferenceDialog;
import com.hbouzidi.fiveprayers.ui.settings.location.AutoCompleteTextPreference;
import com.hbouzidi.fiveprayers.ui.settings.location.AutoCompleteTextPreferenceDialog;
import com.hbouzidi.fiveprayers.ui.settings.timings.NumberPickerPreference;
import com.hbouzidi.fiveprayers.ui.settings.timings.NumberPickerPreferenceDialog;
import com.takisoft.preferencex.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String DIALOG_FRAGMENT_TAG = "PreferencesDialogFragment";

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;

        if (preference instanceof AutoCompleteTextPreference) {
            dialogFragment = new AutoCompleteTextPreferenceDialog((AutoCompleteTextPreference) preference);
        }
        if (preference instanceof NumberPickerPreference) {
            dialogFragment = new NumberPickerPreferenceDialog((NumberPickerPreference) preference);
        }
        if (preference instanceof HijriDayAdjustmentPreference) {
            dialogFragment = new HijriDayAdjustmentPreferenceDialog((HijriDayAdjustmentPreference) preference);
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);

            dialogFragment.show(getParentFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
