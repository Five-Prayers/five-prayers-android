package com.bouzidi.prayertimes.ui.settings.hijri;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bouzidi.prayertimes.R;
import com.travijuu.numberpicker.library.NumberPicker;

public class NumberPickerView extends ConstraintLayout {

    private NumberPicker numberPicker;

    public NumberPickerView(Context context) {
        this(context, null);
    }

    public NumberPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.hijri_day_number_pref, this);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
    }

    public void setNumberPickerValue(int adjustment) {
        numberPicker.setValue(adjustment);
    }

    public int getNumberPickerValue() {
        return numberPicker.getValue();
    }
}
