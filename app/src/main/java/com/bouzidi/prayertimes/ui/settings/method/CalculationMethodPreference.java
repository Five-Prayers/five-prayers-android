package com.bouzidi.prayertimes.ui.settings.method;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.bouzidi.prayertimes.timings.TimingsTuneEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CalculationMethodPreference extends ListPreference {

    public CalculationMethodPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        OnPreferenceChangeListener onPreferenceChangeListener = (preference, newValue) -> {
            List<TimingsTuneEnum> timingsTuneEnums = Arrays.asList(TimingsTuneEnum.values());
            List<String> stringList = timingsTuneEnums.stream().map(String::valueOf).collect(Collectors.toList());

            if (stringList.contains(String.valueOf(newValue))) {
                TimingsTuneEnum timingsTuneEnum = TimingsTuneEnum.valueOf(String.valueOf(newValue));

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("timing_adjustment", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt("fajr_timing_adjustment", timingsTuneEnum.getFajr());
                editor.putInt("dohr_timing_adjustment", timingsTuneEnum.getDhuhr());
                editor.putInt("asr_timing_adjustment", timingsTuneEnum.getAsr());
                editor.putInt("maghreb_timing_adjustment", timingsTuneEnum.getMaghrib());
                editor.putInt("icha_timing_adjustment", timingsTuneEnum.getIsha());

                editor.apply();
            }

            return true;
        };

        setOnPreferenceChangeListener(onPreferenceChangeListener);
    }
}
