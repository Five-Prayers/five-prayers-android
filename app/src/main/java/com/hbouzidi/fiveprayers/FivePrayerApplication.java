package com.hbouzidi.fiveprayers;

import androidx.multidex.MultiDexApplication;

import com.hbouzidi.fiveprayers.ui.report.ErrorActivity;

import cat.ereza.customactivityoncrash.config.CaocConfig;

public class FivePrayerApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        CaocConfig.Builder.create()
                .errorActivity(ErrorActivity.class)
                .apply();
    }
}
