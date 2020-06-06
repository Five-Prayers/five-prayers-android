package com.bouzidi.prayertimes.utils;

import com.bouzidi.prayertimes.timings.PrayerEnum;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

public class PrayerUtils {

    public static PrayerEnum getNextPrayer(@NonNull Map<PrayerEnum, String> prayers, @NonNull Date currentTime) {

        if (TimingUtils.isBeforeOrEqualsTiming(currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.FAJR)), false)) {
            return PrayerEnum.FAJR;
        } else if (TimingUtils.isBetweenTiming(Objects.requireNonNull(prayers.get(PrayerEnum.FAJR)), currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.DHOHR)))) {
            return PrayerEnum.DHOHR;
        } else if (TimingUtils.isBetweenTiming(Objects.requireNonNull(prayers.get(PrayerEnum.DHOHR)), currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.ASR)))) {
            return PrayerEnum.ASR;
        } else if (TimingUtils.isBetweenTiming(Objects.requireNonNull(prayers.get(PrayerEnum.ASR)), currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.MAGHRIB)))) {
            return PrayerEnum.MAGHRIB;
        } else
            return PrayerEnum.ICHA;
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
}
