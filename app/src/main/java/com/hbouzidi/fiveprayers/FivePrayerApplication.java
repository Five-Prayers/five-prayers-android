package com.hbouzidi.fiveprayers;

import androidx.multidex.MultiDexApplication;

import com.hbouzidi.fiveprayers.ui.report.ErrorActivity;

import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class FivePrayerApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        CaocConfig.Builder.create()
                .errorActivity(ErrorActivity.class)
                .apply();
    }
}
