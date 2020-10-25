package com.hbouzidi.fiveprayers.timings;

import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimingsService;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.londonprayertimes.LondonUnifiedPrayerTimingsService;

public class TimingServiceFactory {

    public static TimingsService create(CalculationMethodEnum calculationMethodEnum) {
        if (CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES.equals(calculationMethodEnum)) {
            return LondonUnifiedPrayerTimingsService.getInstance();
        }
        return AladhanTimingsService.getInstance();
    }
}
