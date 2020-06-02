package com.bouzidi.prayer_times.utils;

import com.bouzidi.prayer_times.timings.Prayer;
import com.bouzidi.prayer_times.timings.PrayerEnum;

import java.util.Date;

import androidx.annotation.NonNull;

public class PrayerUtils {

    public static int getNextPrayerIndex(@NonNull Prayer[] prayers, @NonNull Date currentTime) {
        int index = -1;

        for (int i = 0; i < prayers.length; i++) {
            index = i;
            if (prayers[i].getKey().equals(PrayerEnum.FAJR) && TimingUtils.isBeforeTiming(currentTime, prayers[i].getTiming())) {
                return 0;
            }

            if (prayers[i].getKey().equals(PrayerEnum.ICHA) && TimingUtils.isAfterTiming(currentTime, prayers[i].getTiming())) {
                return -1;
            }

            if (TimingUtils.isBetweenTiming(prayers[i].getTiming(), currentTime, prayers[i + 1].getTiming())) {
                return i + 1;
            }
        }
        return index;
    }
}
