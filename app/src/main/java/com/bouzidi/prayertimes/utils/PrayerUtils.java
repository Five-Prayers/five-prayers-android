package com.bouzidi.prayertimes.utils;

import com.bouzidi.prayertimes.timings.PrayerEnum;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

public class PrayerUtils {

    public static PrayerEnum getNextPrayer(@NonNull Map<PrayerEnum, String> prayers, @NonNull Date currentTime) {
        if (isTimingAfterMidnight(Objects.requireNonNull(prayers.get(PrayerEnum.ICHA)), prayers)) {
            if (TimingUtils.isStrictlyBeforeTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.ICHA)))) {
                return PrayerEnum.ICHA;
            }
            if (TimingUtils.isStrictlyBeforeTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.FAJR)))) {
                return PrayerEnum.FAJR;
            } else if (TimingUtils.isStrictlyBeforeTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.DHOHR)))) {
                return PrayerEnum.DHOHR;
            } else if (TimingUtils.isStrictlyBeforeTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.ASR)))) {
                return PrayerEnum.ASR;
            } else if (TimingUtils.isStrictlyBeforeTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.MAGHRIB)))) {
                return PrayerEnum.MAGHRIB;
            } else
                return PrayerEnum.ICHA;
        } else {
            if (TimingUtils.isAfterOrEqualsTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.ICHA)))) {
                return PrayerEnum.FAJR;
            }
            if (TimingUtils.isStrictlyBeforeTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.FAJR)))) {
                return PrayerEnum.FAJR;
            }
            if (TimingUtils.isAfterOrEqualsTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.MAGHRIB)))) {
                return PrayerEnum.ICHA;
            }
            if (TimingUtils.isAfterOrEqualsTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.ASR)))) {
                return PrayerEnum.MAGHRIB;
            }
            if (TimingUtils.isAfterOrEqualsTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.DHOHR)))) {
                return PrayerEnum.ASR;
            }
            return PrayerEnum.DHOHR;
        }
    }

    public static PrayerEnum getPreviousPrayerKey(PrayerEnum key) {
        switch (key) {
            case FAJR:
                return PrayerEnum.ICHA;
            case DHOHR:
                return PrayerEnum.FAJR;
            case ASR:
                return PrayerEnum.DHOHR;
            case MAGHRIB:
                return PrayerEnum.ASR;
            default:
                return PrayerEnum.MAGHRIB;
        }
    }

    private static boolean isTimingAfterMidnight(String timings, @NonNull Map<PrayerEnum, String> prayers) {
        return TimingUtils.isBeforeOnSameDay(timings, Objects.requireNonNull(prayers.get(PrayerEnum.DHOHR)));
    }
}
