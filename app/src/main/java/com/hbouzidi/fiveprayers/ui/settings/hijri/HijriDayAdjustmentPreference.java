package com.hbouzidi.fiveprayers.ui.settings.hijri;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;

import java.text.DecimalFormat;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class HijriDayAdjustmentPreference extends DialogPreference {

    private int adjustment;


    public HijriDayAdjustmentPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        adjustment = defaultSharedPreferences.getInt(getKey(), 0);

        updateSummary();
    }

    public void persist() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        editor.putInt(getKey(), adjustment);
        editor.apply();

        updateSummary();
    }

    public int getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(int adjustment) {
        this.adjustment = adjustment;
    }

    private void updateSummary() {
        setSummary(formatNumber(adjustment));
    }

    private String formatNumber(int number) {
        DecimalFormat fmt = new DecimalFormat("+#,##0;-#");
        return fmt.format(number);
    }
}
