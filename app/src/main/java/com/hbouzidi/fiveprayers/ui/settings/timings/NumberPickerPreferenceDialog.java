package com.hbouzidi.fiveprayers.ui.settings.timings;

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
public class NumberPickerPreferenceDialog extends PreferenceDialogFragmentCompat {

    private final NumberPickerPreference preference;
    private MultipleNumberPickerView multipleNumberPickerView;

    public NumberPickerPreferenceDialog(NumberPickerPreference preference) {
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

        multipleNumberPickerView = new MultipleNumberPickerView(context);
        setPickersInitialValues();

        return multipleNumberPickerView;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            updatePreferenceValues();
            preference.persist();
        }
    }

    private void setPickersInitialValues() {
        int fajrTimingAdjustment = preference.getFajrTimingAdjustment();
        int dohrTimingAdjustment = preference.getDohrTimingAdjustment();
        int asrTimingAdjustment = preference.getAsrTimingAdjustment();
        int maghrebTimingAdjustment = preference.getMaghrebTimingAdjustment();
        int ichaTimingAdjustment = preference.getIchaTimingAdjustment();

        multipleNumberPickerView.setFajrNumberPickerValue(fajrTimingAdjustment);
        multipleNumberPickerView.setDohrNumberPickerValue(dohrTimingAdjustment);
        multipleNumberPickerView.setAsrNumberPickerValue(asrTimingAdjustment);
        multipleNumberPickerView.setMaghrebNumberPickerValue(maghrebTimingAdjustment);
        multipleNumberPickerView.setIchaNumberPickerValue(ichaTimingAdjustment);
    }

    private void updatePreferenceValues() {

        int fajrNumberPickerValue = multipleNumberPickerView.getFajrNumberPickerValue();
        int dohrNumberPickerValue = multipleNumberPickerView.getDohrNumberPickerValue();
        int asrNumberPickerValue = multipleNumberPickerView.getAsrNumberPickerValue();
        int maghrebNumberPickerValue = multipleNumberPickerView.getMaghrebNumberPickerValue();
        int ichaNumberPickerValue = multipleNumberPickerView.getIchaNumberPickerValue();

        preference.setFajrTimingAdjustment(fajrNumberPickerValue);
        preference.setDohrTimingAdjustment(dohrNumberPickerValue);
        preference.setAsrTimingAdjustment(asrNumberPickerValue);
        preference.setMaghrebTimingAdjustment(maghrebNumberPickerValue);
        preference.setIchaTimingAdjustment(ichaNumberPickerValue);
    }
}
