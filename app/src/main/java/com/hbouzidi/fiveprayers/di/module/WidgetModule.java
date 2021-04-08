package com.hbouzidi.fiveprayers.di.module;


import android.content.Context;

import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.osm.NominatimAPIService;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimingsService;
import com.hbouzidi.fiveprayers.timings.londonprayertimes.LondonUnifiedPrayerTimingsService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Module
public class WidgetModule {

    @Singleton
    @Provides
    public LocationHelper providesLocationHelper(Context context) {
        return new LocationHelper(context);
    }

    @Singleton
    @Provides
    public AddressHelper providesAddressHelper(Context context, NominatimAPIService nominatimAPIService) {
        return new AddressHelper(context, nominatimAPIService);
    }

    @Singleton
    @Provides
    public TimingServiceFactory providesTimingServiceFactory(LondonUnifiedPrayerTimingsService londonUnifiedPrayerTimingsService,
                                                             AladhanTimingsService aladhanTimingsService) {
        return new TimingServiceFactory(londonUnifiedPrayerTimingsService, aladhanTimingsService);
    }
}
