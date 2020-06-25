package com.bouzidi.prayertimes.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.ui.settings.autocomplete.AutoCompleteTextPreference;
import com.bouzidi.prayertimes.ui.settings.autocomplete.AutoCompleteTextPreferenceDialog;
import com.takisoft.preferencex.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String AUTO_COMPLETE_TEXT_DIALOG_FRAGMENT_TAG = "AutoCompleteTextDialogFragment";

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

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);

            dialogFragment.show(getParentFragmentManager(), AUTO_COMPLETE_TEXT_DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
