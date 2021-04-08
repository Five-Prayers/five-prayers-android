package com.hbouzidi.fiveprayers;

import android.os.Build;

import androidx.multidex.MultiDexApplication;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.hbouzidi.fiveprayers.common.api.TLSSocketFactoryCompat;
import com.hbouzidi.fiveprayers.di.component.ApplicationComponent;
import com.hbouzidi.fiveprayers.di.component.DaggerApplicationComponent;
import com.hbouzidi.fiveprayers.di.component.DaggerServiceComponent;
import com.hbouzidi.fiveprayers.di.component.DaggerWidgetComponent;
import com.hbouzidi.fiveprayers.di.component.ServiceComponent;
import com.hbouzidi.fiveprayers.di.component.WidgetComponent;
import com.hbouzidi.fiveprayers.di.factory.worker.WorkerProviderFactory;
import com.hbouzidi.fiveprayers.di.module.AppModule;
import com.hbouzidi.fiveprayers.di.module.WidgetModule;
import com.hbouzidi.fiveprayers.ui.report.ErrorActivity;

import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class FivePrayerApplication extends MultiDexApplication {

    public ApplicationComponent appComponent = DaggerApplicationComponent
            .builder()
            .appModule(new AppModule(this))
            .build();

    public WidgetComponent widgetComponent = DaggerWidgetComponent
            .builder()
            .appModule(new AppModule(this))
            .widgetModule(new WidgetModule())
            .build();

    public ServiceComponent serviceComponent = DaggerServiceComponent
            .builder()
            .appModule(new AppModule(this))
            .build();

    @Override
    public void onCreate() {
        super.onCreate();

        // enable TLS1.1/1.2 for kitkat devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            TLSSocketFactoryCompat.setAsDefault();
        }

        CaocConfig
                .Builder
                .create()
                .errorActivity(ErrorActivity.class)
                .apply();

        configureWorkManager();
    }

    private void configureWorkManager() {
        WorkerProviderFactory factory = appComponent.workerProviderFactory();
        Configuration config = new Configuration.Builder()
                .setWorkerFactory(factory)
                .build();

        WorkManager.initialize(this, config);
    }
}
