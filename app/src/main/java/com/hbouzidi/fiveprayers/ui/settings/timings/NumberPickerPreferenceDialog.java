package com.hbouzidi.fiveprayers.ui.settings.timings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.preference.PreferenceDialogFragmentCompat;

import com.travijuu.numberpicker.library.NumberPicker;

public class NumberPickerPreferenceDialog extends PreferenceDialogFragmentCompat {

    private Context context;

    public static final int MAX_VALUE = 10;
    public static final int MIN_VALUE = 0;

    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;
    private int value;

    private final NumberPickerPreference preference;
    private NumberPicker numberPicker;
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
        this.context = context;

        multipleNumberPickerView = new MultipleNumberPickerView(this.context);
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
