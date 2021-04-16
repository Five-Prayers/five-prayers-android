package com.hbouzidi.fiveprayers.timings;

import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimingsService;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.londonprayertimes.LondonUnifiedPrayerTimingsService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class TimingServiceFactory {

    private final LondonUnifiedPrayerTimingsService londonUnifiedPrayerTimingsService;
    private final AladhanTimingsService aladhanTimingsService;

    @Inject
    public TimingServiceFactory(LondonUnifiedPrayerTimingsService londonUnifiedPrayerTimingsService,
                                AladhanTimingsService aladhanTimingsService) {

        this.londonUnifiedPrayerTimingsService = londonUnifiedPrayerTimingsService;
        this.aladhanTimingsService = aladhanTimingsService;
    }

    public TimingsService create(CalculationMethodEnum calculationMethodEnum) {
        if (CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES.equals(calculationMethodEnum)) {
            return londonUnifiedPrayerTimingsService;
        }
        return aladhanTimingsService;
    }
}
