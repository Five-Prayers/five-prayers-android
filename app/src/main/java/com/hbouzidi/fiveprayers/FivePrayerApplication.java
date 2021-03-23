package com.hbouzidi.fiveprayers;

import android.os.Build;

import androidx.multidex.MultiDexApplication;

import com.hbouzidi.fiveprayers.common.api.TLSSocketFactoryCompat;
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

        // enable TLS1.1/1.2 for kitkat devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            TLSSocketFactoryCompat.setAsDefault();
        }

        CaocConfig.Builder.create()
                .errorActivity(ErrorActivity.class)
                .apply();
    }
}
