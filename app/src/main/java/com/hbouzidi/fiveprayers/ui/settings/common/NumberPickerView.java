package com.hbouzidi.fiveprayers.ui.settings.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hbouzidi.fiveprayers.R;
import com.travijuu.numberpicker.library.NumberPicker;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NumberPickerView extends ConstraintLayout {

    private final NumberPicker numberPicker;

    public NumberPickerView(Context context) {
        this(context, null);
    }

    public NumberPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.number_picker_pref, this);

        numberPicker = findViewById(R.id.numberPicker);
    }

    public void setMaxValue(int maxValue) {
        numberPicker.setMax(maxValue);
    }

    public void setMinValue(int minValue) {
        numberPicker.setMin(minValue);
    }

    public void setUnitValue(int unitValue) {
        numberPicker.setUnit(unitValue);
    }

    public void setValue(int value) {
        numberPicker.setValue(value);
    }

    public int getValue() {
        return numberPicker.getValue();
    }
}
