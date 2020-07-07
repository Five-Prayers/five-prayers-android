package com.bouzidi.prayertimes.timings.calculations;

import android.content.Context;
import android.content.SharedPreferences;

public class CalculationMethodHelper {

    public static void updateTimingAdjustmentPreference(String methodName, Context context) {
        TimingsTuneEnum timingsTuneEnum = TimingsTuneEnum.getValueByName(methodName);

        SharedPreferences sharedPreferences = context.getSharedPreferences("timing_adjustment", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("fajr_timing_adjustment", timingsTuneEnum.getFajr());
        editor.putInt("dohr_timing_adjustment", timingsTuneEnum.getDhuhr());
        editor.putInt("asr_timing_adjustment", timingsTuneEnum.getAsr());
        editor.putInt("maghreb_timing_adjustment", timingsTuneEnum.getMaghrib());
        editor.putInt("icha_timing_adjustment", timingsTuneEnum.getIsha());

        editor.apply();
    }
}
