package com.hbouzidi.fiveprayers.ui.settings.hijri;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.preference.PreferenceDialogFragmentCompat;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class HijriDayAdjustmentPreferenceDialog extends PreferenceDialogFragmentCompat {

    private final HijriDayAdjustmentPreference preference;
    private NumberPickerView numberPickerView;

    public HijriDayAdjustmentPreferenceDialog(HijriDayAdjustmentPreference preference) {
        this.preference = preference;

        final Bundle b = new Bundle();
        b.putString(ARG_KEY, preference.getKey());
        setArguments(b);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nexus 7 needs the keyboard hiding explicitly.
        // A flag on the activity in the manifest doesn't
        // apply to the dialog, so needs to be in code:
        Window window = requireActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected View onCreateDialogView(Context context) {

        numberPickerView = new NumberPickerView(context);

        setPickerInitialValues();

        return numberPickerView;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            updatePreferenceValues();
            preference.persist();
        }
    }

    private void setPickerInitialValues() {
        int adjustment = preference.getAdjustment();
        numberPickerView.setNumberPickerValue(adjustment);
    }

    private void updatePreferenceValues() {
        int numberPickerValue = numberPickerView.getNumberPickerValue();
        preference.setAdjustment(numberPickerValue);
    }
}
