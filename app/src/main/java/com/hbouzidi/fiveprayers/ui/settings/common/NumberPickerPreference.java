package com.hbouzidi.fiveprayers.ui.settings.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;

import com.hbouzidi.fiveprayers.R;

import java.text.DecimalFormat;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NumberPickerPreference extends DialogPreference {

    private int value;
    private final int minValue;
    private final int maxValue;
    private final int unitValue;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Number_Picker_Preference, 0, 0);

        maxValue = a.getInt(R.styleable.Number_Picker_Preference_maxValue, 3);
        minValue = a.getInt(R.styleable.Number_Picker_Preference_minValue, -3);
        unitValue = a.getInt(R.styleable.Number_Picker_Preference_unitValue, 1);
        int initialValue = a.getInt(R.styleable.Number_Picker_Preference_initialValue, 0);

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        value = defaultSharedPreferences.getInt(getKey(), initialValue);

        a.recycle();

        updateSummary();
    }

    public void persist() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        editor.putInt(getKey(), value);
        editor.apply();

        updateSummary();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getUnitValue() {
        return unitValue;
    }

    private void updateSummary() {
        setSummary(formatNumber(value));
    }

    private String formatNumber(int number) {
        DecimalFormat fmt = new DecimalFormat("+#,##0;-#");
        return fmt.format(number);
    }
}
