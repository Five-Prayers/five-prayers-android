package com.hbouzidi.fiveprayers.utils;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.common.PrayerEnum;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class PrayerUtils {

    public static PrayerEnum getNextPrayer(@NonNull Map<PrayerEnum, LocalDateTime> prayers, @NonNull LocalDateTime currentTime) {
        if (TimingUtils.isBetweenTiming(Objects.requireNonNull(prayers.get(PrayerEnum.MAGHRIB)), currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.ICHA)))) {
            return PrayerEnum.ICHA;
        }
        if (TimingUtils.isBetweenTiming(Objects.requireNonNull(prayers.get(PrayerEnum.ASR)), currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.MAGHRIB)))) {
            return PrayerEnum.MAGHRIB;
        }
        if (TimingUtils.isBetweenTiming(Objects.requireNonNull(prayers.get(PrayerEnum.DHOHR)), currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.ASR)))) {
            return PrayerEnum.ASR;
        }
        if (TimingUtils.isBetweenTiming(Objects.requireNonNull(prayers.get(PrayerEnum.FAJR)), currentTime, Objects.requireNonNull(prayers.get(PrayerEnum.DHOHR)))) {
            return PrayerEnum.DHOHR;
        }
        return PrayerEnum.FAJR;
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
