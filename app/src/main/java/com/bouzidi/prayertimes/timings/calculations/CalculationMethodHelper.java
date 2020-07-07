package com.bouzidi.prayertimes.timings.calculations;

import android.content.Context;
import android.content.SharedPreferences;

import com.bouzidi.prayertimes.utils.Constants;

public class CalculationMethodHelper {

    public static void updateTimingAdjustmentPreference(String methodName, Context context) {
        TimingsTuneEnum timingsTuneEnum = TimingsTuneEnum.getValueByName(methodName);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Constants.FAJR_TIMING_ADJUSTMENT, timingsTuneEnum.getFajr());
        editor.putInt(Constants.DOHR_TIMING_ADJUSTMENT, timingsTuneEnum.getDhuhr());
        editor.putInt(Constants.ASR_TIMING_ADJUSTMENT, timingsTuneEnum.getAsr());
        editor.putInt(Constants.MAGHREB_TIMING_ADJUSTMENT, timingsTuneEnum.getMaghrib());
        editor.putInt(Constants.ICHA_TIMING_ADJUSTMENT, timingsTuneEnum.getIsha());

        editor.apply();
    }
}
