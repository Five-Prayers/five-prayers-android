package com.hbouzidi.fiveprayers.di.module;

import com.hbouzidi.fiveprayers.di.factory.worker.ChildWorkerFactory;
import com.hbouzidi.fiveprayers.di.factory.worker.WorkerKey;
import com.hbouzidi.fiveprayers.job.InstantPrayerScheduler;
import com.hbouzidi.fiveprayers.job.PrayerUpdater;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Module
public interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(PrayerUpdater.class)
    ChildWorkerFactory bindPrayerUpdaterWorker(PrayerUpdater.Factory factory);

    @Binds
    @IntoMap
    @WorkerKey(InstantPrayerScheduler.class)
    ChildWorkerFactory bindInstantPrayerSchedulerWorker(InstantPrayerScheduler.Factory factory);
}
