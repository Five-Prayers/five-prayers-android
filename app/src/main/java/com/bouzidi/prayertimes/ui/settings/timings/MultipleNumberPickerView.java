package com.bouzidi.prayertimes.ui.settings.timings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bouzidi.prayertimes.R;
import com.travijuu.numberpicker.library.NumberPicker;

public class MultipleNumberPickerView extends ConstraintLayout {

    private static final int MAX_VALUE = 30;
    private static final int MIN_VALUE = -30;

    private NumberPicker fajrNumberPicker;
    private NumberPicker dohrNumberPicker;
    private NumberPicker asrNumberPicker;
    private NumberPicker maghrebNumberPicker;
    private NumberPicker ichaNumberPicker;

    public MultipleNumberPickerView(Context context) {
        this(context, null);
    }

    public MultipleNumberPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.number_pref, this);

        fajrNumberPicker = (NumberPicker) findViewById(R.id.fajr_number_picker);
        dohrNumberPicker = (NumberPicker) findViewById(R.id.dohr_number_picker);
        asrNumberPicker = (NumberPicker) findViewById(R.id.asr_number_picker);
        maghrebNumberPicker = (NumberPicker) findViewById(R.id.maghreb_number_picker);
        ichaNumberPicker = (NumberPicker) findViewById(R.id.icha_number_picker);

        fajrNumberPicker.setMax(MAX_VALUE);
        fajrNumberPicker.setMin(MIN_VALUE);

        dohrNumberPicker.setMax(MAX_VALUE);
        dohrNumberPicker.setMin(MIN_VALUE);

        asrNumberPicker.setMax(MAX_VALUE);
        asrNumberPicker.setMin(MIN_VALUE);

        maghrebNumberPicker.setMax(MAX_VALUE);
        maghrebNumberPicker.setMin(MIN_VALUE);

        ichaNumberPicker.setMax(MAX_VALUE);
        ichaNumberPicker.setMin(MIN_VALUE);
    }

    public int getFajrNumberPickerValue() {
        return fajrNumberPicker.getValue();
    }

    public int getDohrNumberPickerValue() {
        return dohrNumberPicker.getValue();
    }

    public int getAsrNumberPickerValue() {
        return asrNumberPicker.getValue();
    }

    public int getMaghrebNumberPickerValue() {
        return maghrebNumberPicker.getValue();
    }

    public int getIchaNumberPickerValue() {
        return ichaNumberPicker.getValue();
    }

    public void setFajrNumberPickerValue(int value) {
        fajrNumberPicker.setValue(value);
    }

    public void setDohrNumberPickerValue(int value) {
        dohrNumberPicker.setValue(value);
    }

    public void setAsrNumberPickerValue(int value) {
        asrNumberPicker.setValue(value);
    }

    public void setMaghrebNumberPickerValue(int value) {
        maghrebNumberPicker.setValue(value);
    }

    public void setIchaNumberPickerValue(int value) {
        ichaNumberPicker.setValue(value);
    }
}
