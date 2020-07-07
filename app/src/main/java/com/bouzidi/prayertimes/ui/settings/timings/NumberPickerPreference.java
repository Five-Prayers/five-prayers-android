package com.bouzidi.prayertimes.ui.settings.timings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.utils.Constants;

import java.text.DecimalFormat;

public class NumberPickerPreference extends DialogPreference {

    private int fajrTimingAdjustment;
    private int dohrTimingAdjustment;
    private int asrTimingAdjustment;
    private int maghrebTimingAdjustment;
    private int ichaTimingAdjustment;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);

        fajrTimingAdjustment = sharedPreferences.getInt(Constants.FAJR_TIMING_ADJUSTMENT, 0);
        dohrTimingAdjustment = sharedPreferences.getInt(Constants.DOHR_TIMING_ADJUSTMENT, 0);
        asrTimingAdjustment = sharedPreferences.getInt(Constants.ASR_TIMING_ADJUSTMENT, 0);
        maghrebTimingAdjustment = sharedPreferences.getInt(Constants.MAGHREB_TIMING_ADJUSTMENT, 0);
        ichaTimingAdjustment = sharedPreferences.getInt(Constants.ICHA_TIMING_ADJUSTMENT, 0);

        updateSummary();
    }

    public void persist() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Constants.FAJR_TIMING_ADJUSTMENT, fajrTimingAdjustment);
        editor.putInt(Constants.DOHR_TIMING_ADJUSTMENT, dohrTimingAdjustment);
        editor.putInt(Constants.ASR_TIMING_ADJUSTMENT, asrTimingAdjustment);
        editor.putInt(Constants.MAGHREB_TIMING_ADJUSTMENT, maghrebTimingAdjustment);
        editor.putInt(Constants.ICHA_TIMING_ADJUSTMENT, ichaTimingAdjustment);

        editor.apply();

        updateSummary();
    }

    public int getFajrTimingAdjustment() {
        return fajrTimingAdjustment;
    }

    public int getDohrTimingAdjustment() {
        return dohrTimingAdjustment;
    }

    public int getAsrTimingAdjustment() {
        return asrTimingAdjustment;
    }

    public int getMaghrebTimingAdjustment() {
        return maghrebTimingAdjustment;
    }

    public int getIchaTimingAdjustment() {
        return ichaTimingAdjustment;
    }

    public void setFajrTimingAdjustment(int fajrTimingAdjustment) {
        this.fajrTimingAdjustment = fajrTimingAdjustment;
    }

    public void setDohrTimingAdjustment(int dohrTimingAdjustment) {
        this.dohrTimingAdjustment = dohrTimingAdjustment;
    }

    public void setAsrTimingAdjustment(int asrTimingAdjustment) {
        this.asrTimingAdjustment = asrTimingAdjustment;
    }

    public void setMaghrebTimingAdjustment(int maghrebTimingAdjustment) {
        this.maghrebTimingAdjustment = maghrebTimingAdjustment;
    }

    public void setIchaTimingAdjustment(int ichaTimingAdjustment) {
        this.ichaTimingAdjustment = ichaTimingAdjustment;
    }

    private void updateSummary() {
        String summary = getContext().getString(R.string.SHORT_FAJR) +
                ": " +
                formatNumber(fajrTimingAdjustment) +
                " | " +
                getContext().getString(R.string.SHORT_DHOHR) +
                ": " +
                formatNumber(dohrTimingAdjustment) +
                " | " +
                getContext().getString(R.string.SHORT_ASR) +
                ": " +
                formatNumber(asrTimingAdjustment) +
                " | " +
                getContext().getString(R.string.SHORT_MAGHRIB) +
                ": " +
                formatNumber(maghrebTimingAdjustment) +
                " | " +
                getContext().getString(R.string.SHORT_ICHA) +
                ": " +
                formatNumber(ichaTimingAdjustment);

        setSummary(summary);
    }

    private String formatNumber(int number) {
        DecimalFormat fmt = new DecimalFormat("+#,##0;-#");
        return fmt.format(number);
    }
}
